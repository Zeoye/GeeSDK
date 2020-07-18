package com.gtdev5.geetolsdk.mylibrary.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.base.BaseGTActivity;
import com.gtdev5.geetolsdk.mylibrary.beans.AliOssBean;
import com.gtdev5.geetolsdk.mylibrary.beans.ResultBean;
import com.gtdev5.geetolsdk.mylibrary.callback.BaseCallback;
import com.gtdev5.geetolsdk.mylibrary.feedback.adapters.GTImageAddAdapter;
import com.gtdev5.geetolsdk.mylibrary.feedback.bean.PicInfo;
import com.gtdev5.geetolsdk.mylibrary.feedback.utils.AliOssBatchPicUtils;
import com.gtdev5.geetolsdk.mylibrary.feedback.utils.GTUtils;
import com.gtdev5.geetolsdk.mylibrary.http.HttpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.MD5Tools;
import com.gtdev5.geetolsdk.mylibrary.util.ToastUtils;
import com.gtdev5.geetolsdk.mylibrary.util.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZL on 2019/12/19
 *
 * 反馈回复
 */
public class GTSuggestReplyActivity extends BaseGTActivity {
    private TextView mAddText;
    private EditText mContentEdit;
    private RecyclerView mPicRecyclerView;
    private GTImageAddAdapter mImageAddAdapter;
    private ProgressBar mProgressBar;
    private int mServiceId;

    private List<PicInfo> mPicInfos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gt_activity_suggest_reply);
        GTUtils.setImmersionStatusBar(this);
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mServiceId = getIntent().getIntExtra("data", -1);;
        ImageView back = findViewById(R.id.iv_back);
        mContentEdit = findViewById(R.id.et_content);
        mPicRecyclerView = findViewById(R.id.recyclerview);
        mProgressBar = findViewById(R.id.progressBar);
        mAddText = findViewById(R.id.tv_add);
        back.setOnClickListener(v -> onBackPressed());
        mAddText.setOnClickListener(v -> addReply());
        initRecyclerView();
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        mPicRecyclerView.setLayoutManager(manager);
        mImageAddAdapter = new GTImageAddAdapter(this, 3, null, new GTImageAddAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                choosePic(1);
            }

            @Override
            public void OnAddItemClick(int count) {
                choosePic(count);
            }
        });
        mPicRecyclerView.setAdapter(mImageAddAdapter);
    }

    /**
     * 添加回复
     */
    private void addReply() {
        String content = mContentEdit.getText().toString();
        if (Utils.isEmpty(content)) {
            ToastUtils.showShortToast("回复内容不能为空哦");
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mAddText.setEnabled(false);
        if (mPicInfos.size() > 0) {
            AliOssBean aliOssBean = Utils.getAliOssParam();
            if (aliOssBean != null) {
                AliOssBatchPicUtils.getInstance(this).uploadBatchFile(aliOssBean.getBucketName(),
                        mPicInfos, (success, failure) -> {
                            if (success != null && success.size() > 0) {
                                List<String> imgList = new ArrayList<>();
                                for (PicInfo picInfo : success) {
                                    imgList.add(picInfo.getName());
                                }
                                addReplyData(content, Utils.list2String(imgList));
                            }
                        });
            }
        } else {
            addReplyData(content, "");
        }
    }

    /**
     * 添加回复
     */
    private void addReplyData(String content, String img_url) {
        HttpUtils.getInstance().postAddRepley(mServiceId, content, img_url,
                new BaseCallback<ResultBean>() {
                    @Override
                    public void onRequestBefore() {}

                    @Override
                    public void onFailure(Request request, Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        mAddText.setEnabled(true);
                        ToastUtils.showShortToast("反馈失败" + e.toString());
                    }

                    @Override
                    public void onSuccess(Response response, ResultBean o) {
                        if (o.isIssucc()) {
                            ToastUtils.showShortToast("回复成功！");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            mAddText.setEnabled(true);
                            ToastUtils.showShortToast("反馈失败" + o.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response response, int errorCode, Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        mAddText.setEnabled(true);
                        ToastUtils.showShortToast("反馈失败" + e.toString());
                    }
                });
    }

    /**
     * 选择图片
     */
    private void choosePic(int count) {
        RxGalleryFinal rxGalleryFinal = RxGalleryFinal.with(this).image().multiple();
        rxGalleryFinal.maxSize(count)
                .imageLoader(ImageLoaderType.UNIVERSAL)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        List<MediaBean> list = imageMultipleResultEvent.getResult();
                        if (list != null && list.size() > 0) {
                            List<String> strings = new ArrayList<>();
                            for (MediaBean mediaBean : list) {
                                strings.add(mediaBean.getOriginalPath());
                                try {
                                    String path = mediaBean.getOriginalPath();
                                    String name = GTUtils.getPicName(path);
                                    name = MD5Tools.MD5(name) + ".jpg";
                                    PicInfo picInfo = new PicInfo(name, path);
                                    mPicInfos.add(picInfo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mImageAddAdapter.AddDatas(strings);
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                })
                .openGallery();
    }
}

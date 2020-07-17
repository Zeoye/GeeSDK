package com.gtdev5.geetolsdk.mylibrary.feedback;

import android.os.Bundle;
import android.os.Handler;
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
import com.gtdev5.geetolsdk.mylibrary.feedback.adapters.GTTypeChooseAdapter;
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
 * Created by ZL on 2019/12/18
 *
 * 添加反馈
 */
public class GTSuggestAddActivity extends BaseGTActivity {
    private TextView mAddText;
    private EditText mTitleEdit, mContentEdit;
    private RecyclerView mPicRecyclerView, mTypeRecyclerView;
    private GTImageAddAdapter mImageAddAdapter;
    private GTTypeChooseAdapter mTypeChooseAdapter;
    private ProgressBar mProgressBar;

    private List<PicInfo> mPicInfos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gt_activity_suggest_add);
        GTUtils.setImmersionStatusBar(this);
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        ImageView back = findViewById(R.id.iv_back);
        mTitleEdit = findViewById(R.id.et_title);
        mContentEdit = findViewById(R.id.et_content);
        mPicRecyclerView = findViewById(R.id.recyclerview);
        mTypeRecyclerView = findViewById(R.id.recyclerview1);
        mProgressBar = findViewById(R.id.progressBar);
        mAddText = findViewById(R.id.tv_add);
        back.setOnClickListener(v -> onBackPressed());
        mAddText.setOnClickListener(v -> addSuggest());
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
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
        GridLayoutManager manager1 = new GridLayoutManager(this, 3);
        mTypeRecyclerView.setLayoutManager(manager1);
        List<String> types = new ArrayList<>();
        types.add("程序BUG");
        types.add("内容建议");
        types.add("网络问题");
        types.add("功能建议");
        types.add("其他");
        mTypeChooseAdapter = new GTTypeChooseAdapter(types);
        mTypeRecyclerView.setAdapter(mTypeChooseAdapter);
        mTypeChooseAdapter.setOnItemClickListener((adapter, view, position) -> mTypeChooseAdapter.setCurrentPosition(position));
    }

    /**
     * 添加反馈
     */
    private void addSuggest() {
        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();
        if (Utils.isEmpty(title)) {
            ToastUtils.showShortToast("反馈标题不能为空哦");
            return;
        }
        if (Utils.isEmpty(content)) {
            ToastUtils.showShortToast("反馈内容不能为空哦");
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
                                StringBuilder sb = new StringBuilder("");
                                for (PicInfo picInfo : success) {
                                    sb.append(picInfo.getName());
                                    sb.append(",");
                                }
                                addSuggestData(title, content, sb.toString());
                            }
                        });
            }
        } else {
            addSuggestData(title, content, "");
        }
    }

    /**
     * 添加反馈
     */
    private void addSuggestData(String title, String content, String img_url) {
        HttpUtils.getInstance().postAddService(title, content,
                mTypeChooseAdapter == null ? "" : mTypeChooseAdapter.getCurentType(), img_url,
                new BaseCallback<ResultBean>() {
                    @Override
                    public void onRequestBefore() {}

                    @Override
                    public void onFailure(Request request, Exception e) {
                        new Handler(getMainLooper()).post(() -> {
                            mProgressBar.setVisibility(View.GONE);
                            mAddText.setEnabled(true);
                            ToastUtils.showShortToast("反馈失败" + e.toString());
                        });
                    }

                    @Override
                    public void onSuccess(Response response, ResultBean o) {
                        if (o.isIssucc()) {
                            mProgressBar.setVisibility(View.GONE);
                            mAddText.setEnabled(true);
                            ToastUtils.showShortToast("提交成功,感谢您的反馈,有你我们会做的更好,还请您及时关注您的反馈状态哦");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            new Handler(getMainLooper()).post(() -> {
                                mProgressBar.setVisibility(View.GONE);
                                mAddText.setEnabled(true);
                                ToastUtils.showShortToast("反馈失败" + o.getMsg());
                            });
                        }
                    }

                    @Override
                    public void onError(Response response, int errorCode, Exception e) {
                        new Handler(getMainLooper()).post(() -> {
                            mProgressBar.setVisibility(View.GONE);
                            mAddText.setEnabled(true);
                            ToastUtils.showShortToast("反馈失败" + e.toString());
                        });
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

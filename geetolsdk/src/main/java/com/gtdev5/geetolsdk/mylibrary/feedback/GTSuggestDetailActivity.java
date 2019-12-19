package com.gtdev5.geetolsdk.mylibrary.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.base.BaseGTActivity;
import com.gtdev5.geetolsdk.mylibrary.beans.DataResultBean;
import com.gtdev5.geetolsdk.mylibrary.beans.ResultBean;
import com.gtdev5.geetolsdk.mylibrary.beans.ServiceDetailBean;
import com.gtdev5.geetolsdk.mylibrary.callback.BaseCallback;
import com.gtdev5.geetolsdk.mylibrary.feedback.adapters.GTImageShowAdapter;
import com.gtdev5.geetolsdk.mylibrary.feedback.adapters.GTReplyAdapter;
import com.gtdev5.geetolsdk.mylibrary.feedback.utils.GTUtils;
import com.gtdev5.geetolsdk.mylibrary.http.HttpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.ToastUtils;

import java.util.Collections;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZL on 2019/12/18
 *
 * 反馈详情
 */
public class GTSuggestDetailActivity extends BaseGTActivity {
    private TextView mTimeText, mStateText, mTitleText, mTypeText, mContentText, mExitText,
            mReplyText;
    private RecyclerView mPicRecyclerView, mReplyRecyclerView;
    private NestedScrollView mScrollView;
    private ProgressBar mProgressBar;
    private int mServiceId;
    private GTImageShowAdapter mImageShowAdapter;
    private GTReplyAdapter mReplyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gt_activity_suggest_detail);
        GTUtils.setImmersionStatusBar(this);
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mServiceId = getIntent().getIntExtra("data", -1);
        ImageView back = findViewById(R.id.iv_back);
        mExitText = findViewById(R.id.tv_exit);
        mReplyText = findViewById(R.id.tv_reply);
        mTimeText = findViewById(R.id.tv_time);
        mStateText = findViewById(R.id.tv_state);
        mTitleText = findViewById(R.id.tv_suggest_title);
        mTypeText = findViewById(R.id.tv_type);
        mContentText = findViewById(R.id.tv_content);
        mPicRecyclerView = findViewById(R.id.recyclerview);
        mReplyRecyclerView = findViewById(R.id.recyclerview1);
        mProgressBar = findViewById(R.id.progressBar);
        mScrollView = findViewById(R.id.scrollView);
        back.setOnClickListener(v -> onBackPressed());
        mExitText.setOnClickListener(v -> endService());
        mReplyText.setOnClickListener(v -> {
            Intent intent = new Intent(GTSuggestDetailActivity.this, GTSuggestReplyActivity.class);
            intent.putExtra("data", mServiceId);
            startActivityForResult(intent, 121);
        });
        initRecyclerView();
        if (mServiceId != -1) {
            getData();
        }
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        mImageShowAdapter = new GTImageShowAdapter(null);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        mPicRecyclerView.setLayoutManager(manager);
        mPicRecyclerView.setAdapter(mImageShowAdapter);
        mPicRecyclerView.setNestedScrollingEnabled(false);
        mReplyRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        mReplyRecyclerView.setLayoutManager(manager1);
        mReplyAdapter = new GTReplyAdapter(null);
        mReplyRecyclerView.setAdapter(mReplyAdapter);
        mImageShowAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(GTSuggestDetailActivity.this, GTImageShowActivity.class);
            intent.putExtra(GTImageShowActivity.IMG_POS, position);
            Gson gson = new Gson();
            intent.putExtra(GTImageShowActivity.IMG_DATAS, gson.toJson(mImageShowAdapter.getData()));
            startActivity(intent);
        });
    }

    /**
     * 获取数据
     */
    private void getData() {
        HttpUtils.getInstance().postGetServicesDetails(mServiceId, new BaseCallback<DataResultBean<ServiceDetailBean>>() {
            @Override
            public void onRequestBefore() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Response response, DataResultBean<ServiceDetailBean> o) {
                mProgressBar.setVisibility(View.GONE);
                if (o != null && o.getIssucc()) {
                    if (o.getData() == null) {
                        ToastUtils.showShortToast("数据无效");
                        return;
                    }
                    mTimeText.setText(o.getData().getAddtime());
                    String temp = "待回复";
                    switch (o.getData().getStatus()) {
                        case 0:
                        case 1:
                            temp = "待回复";
                            break;
                        case 2:
                            temp = "已回复";
                            break;
                        case 99:
                            temp = "已解决";
                            mExitText.setVisibility(View.GONE);
                            mReplyText.setVisibility(View.GONE);
                            break;
                    }
                    mStateText.setText(temp);
                    mTypeText.setText("类型：" + o.getData().getType());
                    mTitleText.setText("标题：" + o.getData().getTitle());
                    mContentText.setText(o.getData().getDescribe());
                    mImageShowAdapter.replaceData(o.getData().getImg());
                    Collections.reverse(o.getData().getReply());
                    mReplyAdapter.replaceData(o.getData().getReply());
                    mScrollView.post(() -> mScrollView.fullScroll(View.FOCUS_DOWN));
                }
            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 结束服务
     */
    private void endService() {
        HttpUtils.getInstance().postEndService(mServiceId, new BaseCallback<ResultBean>() {
            @Override
            public void onRequestBefore() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Response response, ResultBean o) {
                mProgressBar.setVisibility(View.GONE);
                if (o != null && o.isIssucc()) {
                    ToastUtils.showShortToast("服务已结束");
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121 && resultCode == RESULT_OK) {
            getData();
        }
    }
}

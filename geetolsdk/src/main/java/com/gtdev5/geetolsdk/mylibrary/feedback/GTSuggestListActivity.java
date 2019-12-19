package com.gtdev5.geetolsdk.mylibrary.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.base.BaseGTActivity;
import com.gtdev5.geetolsdk.mylibrary.beans.ListResultBean;
import com.gtdev5.geetolsdk.mylibrary.beans.ServiceItemBean;
import com.gtdev5.geetolsdk.mylibrary.callback.BaseCallback;
import com.gtdev5.geetolsdk.mylibrary.feedback.adapters.GTSuggestListAdapter;
import com.gtdev5.geetolsdk.mylibrary.feedback.utils.GTUtils;
import com.gtdev5.geetolsdk.mylibrary.http.HttpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.ToastUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZL on 2019/12/16
 *
 * 反馈列表
 */
public class GTSuggestListActivity extends BaseGTActivity {
    private LinearLayout mEmptyView;
    private PullToRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private GTSuggestListAdapter mAdapter;
    private int mCurrentPage = 1, mLimit = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gt_activity_suggest_list);
        GTUtils.setImmersionStatusBar(this);
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mEmptyView = findViewById(R.id.ll_empty);
        ImageView back = findViewById(R.id.iv_back);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);
        TextView add = findViewById(R.id.tv_add);
        back.setOnClickListener(v -> onBackPressed());
        add.setOnClickListener(v -> startActivityForResult(new Intent(
                GTSuggestListActivity.this, GTSuggestAddActivity.class), 121));
        initRecyclerView();
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        mAdapter = new GTSuggestListAdapter(null);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                getData(true);
            }

            @Override
            public void loadMore() {
                getData(false);
            }
        });
        getData(true);
        mAdapter.setOnItemClickListener((adapter1, view12, position) -> {
            try {
                int id = mAdapter.getData().get(position).getId();
                Intent intent = new Intent(GTSuggestListActivity.this, GTSuggestDetailActivity.class);
                intent.putExtra("data", id);
                startActivityForResult(intent, 121);
            } catch (Exception e) {
                ToastUtils.showShortToast("当前item无效，请刷新重试");
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData(boolean isRefresh) {
        if (isRefresh) {
            mCurrentPage = 1;
        } else {
            mCurrentPage += 1;
        }
        HttpUtils.getInstance().postGetServices(mCurrentPage, mLimit,
                new BaseCallback<ListResultBean<ServiceItemBean>>() {
            @Override
            public void onRequestBefore() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }

            @Override
            public void onSuccess(Response response, ListResultBean<ServiceItemBean> o) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
                if (isRefresh) {
                    if (o != null && o.getItems() != null && o.getItems().size() > 0) {
                        mAdapter.replaceData(o.getItems());
                        mEmptyView.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        ToastUtils.showShortToast("当前没有反馈");
                    }
                } else {
                    if (o != null && o.getItems() != null && o.getItems().size() > 0) {
                        mAdapter.addData(o.getItems());
                        mEmptyView.setVisibility(View.GONE);
                    } else {
                        ToastUtils.showShortToast("没有更多数据了");
                        mEmptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121 && resultCode == RESULT_OK) {
            getData(true);
        }
    }
}

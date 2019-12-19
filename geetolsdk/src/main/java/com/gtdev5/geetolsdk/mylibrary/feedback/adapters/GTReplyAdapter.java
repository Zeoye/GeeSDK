package com.gtdev5.geetolsdk.mylibrary.feedback.adapters;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.beans.ReplyBean;
import com.gtdev5.geetolsdk.mylibrary.feedback.GTImageShowActivity;

import java.util.List;

/**
 * Created by ZL on 2019/12/19
 *
 * 反馈回复适配器
 */
public class GTReplyAdapter extends BaseMultiItemQuickAdapter<ReplyBean, BaseViewHolder> {

    public GTReplyAdapter(List<ReplyBean> data) {
        super(data);
        addItemType(ReplyBean.LEFT, R.layout.gt_item_reply_left);
        addItemType(ReplyBean.RIGHT, R.layout.gt_item_reply_right);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReplyBean item) {
        switch (item.getItemType()) {
            case ReplyBean.LEFT:
                helper.setText(R.id.tv_name, item.getStaff());
                break;
            case ReplyBean.RIGHT:
                helper.setText(R.id.tv_name, "我");
                break;
        }
        helper.setText(R.id.tv_context, item.getDescribe());
        if (item.getImg() != null && item.getImg().size() > 0) {
            int size = 3;
            if (item.getImg().size() < 3) {
                size = item.getImg().size();
            }
            GridLayoutManager manager = new GridLayoutManager(mContext, size);
            RecyclerView recyclerView = helper.getView(R.id.recyclerView);
            recyclerView.setLayoutManager(manager);
            GTImageShowAdapter adapter = new GTImageShowAdapter(item.getImg());
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                Intent intent = new Intent(mContext, GTImageShowActivity.class);
                intent.putExtra(GTImageShowActivity.IMG_POS, position);
                Gson gson = new Gson();
                intent.putExtra(GTImageShowActivity.IMG_DATAS, gson.toJson(adapter1.getData()));
                mContext.startActivity(intent);
            });
        }
    }
}

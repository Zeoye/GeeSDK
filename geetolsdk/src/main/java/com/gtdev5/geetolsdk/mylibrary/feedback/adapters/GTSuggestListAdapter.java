package com.gtdev5.geetolsdk.mylibrary.feedback.adapters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.beans.ServiceItemBean;

import java.util.List;

/**
 * Created by ZL on 2019/12/18
 *
 * 反馈列表适配器
 */
public class GTSuggestListAdapter extends BaseQuickAdapter<ServiceItemBean, BaseViewHolder> {

    public GTSuggestListAdapter(@Nullable List<ServiceItemBean> data) {
        super(R.layout.gt_item_suggest, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServiceItemBean item) {
        helper.setText(R.id.tv_time, item.getAddtime());
        String temp = "待回复";
        switch (item.getStatus()) {
            case 0:
            case 1:
                temp = "待回复";
                break;
            case 2:
                temp = "已回复";
                break;
            case 99:
                temp = "已解决";
                break;
        }
        helper.setText(R.id.tv_state, "状态：" + temp);
        helper.setText(R.id.tv_type, "类型：" + item.getType());
        helper.setText(R.id.tv_title, "标题：" + item.getTitle());
        helper.setText(R.id.tv_content, item.getDescribe());
    }
}

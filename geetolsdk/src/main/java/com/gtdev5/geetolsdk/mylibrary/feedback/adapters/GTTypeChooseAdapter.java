package com.gtdev5.geetolsdk.mylibrary.feedback.adapters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gtdev5.geetolsdk.R;

import java.util.List;

/**
 * Created by ZL on 2019/12/18
 *
 * 反馈类型选择适配器
 */
public class GTTypeChooseAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int currentPosition = 0;

    /**
     * 设置当前选择的item
     */
    public void setCurrentPosition(int currentPosition) {
        int temp = this.currentPosition;
        this.currentPosition = currentPosition;
        this.notifyItemChanged(temp);
        this.notifyItemChanged(currentPosition);
    }

    public GTTypeChooseAdapter(@Nullable List<String> data) {
        super(R.layout.gt_item_suggest_type, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_type, item);
        if (helper.getAdapterPosition() == currentPosition) {
            helper.setImageResource(R.id.iv_icon, R.mipmap.gt_check_yes);
        } else {
            helper.setImageResource(R.id.iv_icon, R.mipmap.gt_check_no);
        }
    }

    public String getCurentType() {
        if (mData != null && mData.size() > currentPosition) {
            return mData.get(currentPosition);
        } else {
            return "其他";
        }
    }
}

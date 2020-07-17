package com.gtdev5.geetolsdk.mylibrary.feedback.adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.beans.ImageBean;

import java.util.List;

/**
 * Created by ZL on 2019/12/18
 *
 * 反馈图片显示适配器
 */
public class GTImageShowAdapter extends BaseQuickAdapter<ImageBean, BaseViewHolder> {

    public GTImageShowAdapter(@Nullable List<ImageBean> data) {
        super(R.layout.gt_item_suggest_image, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageBean item) {
        helper.setVisible(R.id.iv_del, false);
        ImageView imageView = helper.getView(R.id.iv_pic);
        try {
            if (mContext != null) {
                Activity activity = (Activity) mContext;
                if (!activity.isFinishing()) {
                    Glide.with(activity).load(item.getPath()).into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.gtdev5.geetolsdk.mylibrary.feedback.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.gtdev5.geetolsdk.mylibrary.beans.ImageBean;
import com.gtdev5.geetolsdk.mylibrary.feedback.utils.GTUtils;

import java.util.List;

/**
 * Created by ZL on 2019/12/19
 *
 * 图片查看大图适配器
 */
public class GTImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<ImageBean> datas;
    private OnItemClickListener listener;

    public GTImagePagerAdapter(Context context, List<ImageBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        if (listener != null) {
            photoView.setOnClickListener(view -> listener.onItemClick());
        }
        try {
            if (context != null) {
                Activity activity = (Activity) context;
                if (!activity.isFinishing()) {
                    Glide.with(activity).load(GTUtils.showAliOssPic(datas.get(position).getPath())).into(photoView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(photoView);
        return photoView;
    }

    /**
     * 获取当前数据的总页数
     */
    public int getSize() {
        return datas != null ? datas.size() : 0;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }
}

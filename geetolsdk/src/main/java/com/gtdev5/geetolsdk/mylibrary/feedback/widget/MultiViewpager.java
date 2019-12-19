package com.gtdev5.geetolsdk.mylibrary.feedback.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ZL on 2019/12/18
 */
public class MultiViewpager extends ViewPager {

    public MultiViewpager(Context context) {
        super(context);
    }

    public MultiViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean mIsDisallowIntercept = false;

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}

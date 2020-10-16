package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gtdev5.geetolsdk.mylibrary.initialization.GeetolSDK;

/**
 * Created by cheng
 * PackageName ModelTest
 * 2018/1/4 14:24
 * Toast工具类
 */

public class ToastUtils {
    private static Toast mToast = null;

    private static Context mContext;

    public static void init(Context context) {
        if (mContext == null) {
            mContext = context;
            mToast = new Toast(mContext);
        } else {
            Log.e(GeetolSDK.TAG, "未初始化lib");
        }
    }

    /**
     * short Toast
     *
     * @param msg
     */
    public static void showShortToast(String msg) {
        try {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * long Toast
     *
     * @param msg
     */
    public static void showLongToast(String msg) {
        try {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

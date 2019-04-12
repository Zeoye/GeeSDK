package com.gtdev5.geetolsdk.mylibrary.initialization;

import android.content.Context;
import android.text.TextUtils;

import com.gtdev5.geetolsdk.mylibrary.contants.Contants;
import com.gtdev5.geetolsdk.mylibrary.util.CPResourceUtils;
import com.gtdev5.geetolsdk.mylibrary.util.MapUtils;
import com.gtdev5.geetolsdk.mylibrary.util.SpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by cheng
 * PackageName ModelTest
 * 2018/1/6 10:33
 *      所有东西初始化
 */

public class GeetolSDK {
    public static String TAG = "GeetolSDK";

    private static Context mContext;

    public static void init(Context context){
        try {
            if (mContext == null){
                mContext = context;
            }
            SpUtils.getInstance().init(mContext);
            CPResourceUtils.init(mContext);
            ToastUtils.init(mContext);
            MapUtils.init(mContext);

            initCrashReport();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getmContext() {
        return mContext;
    }

    /**
     * 初始化bug提交
     */
    private static void initCrashReport() {
        String crashid = SpUtils.getInstance().getString(Contants.CRESH_REPORT_ID,"");
        if (!TextUtils.isEmpty(crashid)){
            CrashReport.initCrashReport(mContext,crashid,false);
        }
    }

    public static void init(Context context,String commurl){
        init(context);
        SpUtils.getInstance().putString(Contants.COMMON_URL,commurl);
    }





}

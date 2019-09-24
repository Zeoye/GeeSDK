package com.gtdev5.geetolsdk.mylibrary.util;

import android.app.Activity;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.util.H5PayResultModel;

import java.util.Map;

/**
 * Created by ZL on 2019/9/23
 */

public class PayTask extends com.alipay.sdk.app.PayTask {
    public PayTask(Activity activity) {
        super(activity);
    }

    @Override
    public synchronized Map<String, String> payV2(String s, boolean b) {
        return super.payV2(s, b);
    }

    @Override
    public synchronized String pay(String s, boolean b) {
        return super.pay(s, b);
    }

    @Override
    public synchronized H5PayResultModel h5Pay(String s, boolean b) {
        return super.h5Pay(s, b);
    }

    @Override
    public synchronized String fetchOrderInfoFromH5PayUrl(String s) {
        return super.fetchOrderInfoFromH5PayUrl(s);
    }

    @Override
    public synchronized String fetchTradeToken() {
        return super.fetchTradeToken();
    }

    @Override
    public String getVersion() {
        return super.getVersion();
    }

    @Override
    public synchronized boolean payInterceptorWithUrl(String s, boolean b, H5PayCallback h5PayCallback) {
        return super.payInterceptorWithUrl(s, b, h5PayCallback);
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void dismissLoading() {
        super.dismissLoading();
    }
}

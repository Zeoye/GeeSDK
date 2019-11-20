package com.gtdev5.geetolsdk.mylibrary.base;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * Created by ZL on 2019/11/20
 *
 * BaseGTApplication 请继承 避免出现系统调整字体时 软件字体也随系统调整了
 */
public class BaseGTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 设置字体不随系统而变
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}

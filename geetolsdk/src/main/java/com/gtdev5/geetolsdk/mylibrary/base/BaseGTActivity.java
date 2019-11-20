package com.gtdev5.geetolsdk.mylibrary.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ZL on 2019/11/20
 *
 * BaseGTActivity 请继承 避免出现系统调整字体时 软件字体也随系统调整了
 */
public class BaseGTActivity extends AppCompatActivity {
    //设置字体为默认大小，不随系统字体大小改而改变
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
}

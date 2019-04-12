package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gtdev5.geetolsdk.mylibrary.initialization.GeetolSDK;


/**
 * Created by cheng
 * PackageName ModelTest
 * 2018/1/5 8:29
 * 本地数据缓存工具
 */

public class SpUtils {
    private volatile static SpUtils mInstance;

    private Context mComtext;
    private SharedPreferences mPref;

    private SpUtils() {

    }

    /**
     * 单例模式
     *
     * @return
     */
    public static SpUtils getInstance() {
        if (mInstance == null) {
            synchronized (SpUtils.class) {
                if (mInstance == null) {
                    mInstance = new SpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 在Application里面初始化，就能全局调用
     *
     * @param context
     */
    public void init(Context context) {
        if (mComtext == null) {
            mComtext = context;
        } else {
            Log.e(GeetolSDK.TAG, "未初始化lib");
            return;
        }

        mPref = PreferenceManager.getDefaultSharedPreferences(mComtext);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return mPref.getString(key, "");
    }

    public String getString(String key, String value) {
        return mPref.getString(key, value);
    }

    public long getLong(String key) {
        return mPref.getLong(key, 0);
    }

    public long getLong(String key, long value) {
        return mPref.getLong(key, value);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public int getInt(String key, int value) {
        return mPref.getInt(key, value);
    }

    public float getFloat(String key) {
        return mPref.getFloat(key, 0);
    }

    public float getFloat(String key, float value) {
        return mPref.getFloat(key, value);
    }

    public Boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    public Boolean getBoolean(String key, boolean value) {
        return mPref.getBoolean(key, value);
    }

    /**
     * 根据key清除value的缓存
     *
     * @param key
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据缓存
     */
    public void clear() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();
    }
}

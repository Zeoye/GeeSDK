package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author 颜文（daoyi）
 * @version 1.0
 * @date 2019/8/27 15:29
 */
public class MacUtils {

    private static Context mContext;

    public static void init(Context context) {
        if (context != null) {
            mContext = context;
        } else {
            Log.e("doayi", "未初始化lib");
            return;
        }
    }

    public static String getMac(Context context) {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacAddress(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware(context);
        }
        if (mac.equals("02:00:00:00:00:00") || mac.equals("")) {
            mac = CPResourceUtils.getDevice();
        }
        return mac;
    }

    /**
     * 获取mac地址
     */
    public static String getMacAddr(Context context) {
        String mac = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacAddress(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware(context);
        }
        if (mac != null) {
            if (mac.equals("02:00:00:00:00:00")) {
                mac = "";
            }
        }
        return mac;
    }

    //Mac地址获取
    public static String getMacAddress(Context context) {
        String macAddress = null;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
        if (!wifiManager.isWifiEnabled()) {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }

    private static String getMacFromHardware(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0 && res1.length() != 0) {
                    res1.deleteCharAt(res1.length() - 1);
                } else if (res1.length() == 0) {
                    res1.deleteCharAt(res1.length());
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

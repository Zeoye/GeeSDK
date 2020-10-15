package com.gtdev5.geetolsdk.mylibrary.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by ZL on 2019/10/30
 *
 * 设备信息获取工具
 */

public class DeviceUtils {
    // 保存文件的路径
    private static final String CACHE_IMAGE_DIR = "aray/cache/devices";

    // 保存的文件 采用隐藏文件的形式进行保存
    private static final String DEVICES_FILE_NAME = "GEETOL_DEVICES";

    // sp存储deviceId
    public static final String GEETOL_DEVICE_ID = "geetol_device_id";

    /**
     * 获取IMEI(Android版本28及28以下)
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        if (Build.VERSION.SDK_INT <= 28) {
            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        // 适配双卡情况
                        if (tm != null) {
                            Method method = tm.getClass().getMethod("getImei", int.class);
                            if (!TextUtils.isEmpty((String) method.invoke(tm, 0))) {
                                return (String) method.invoke(tm, 0);
                            } else {
                                return getUUID();
                            }
                        } else {
                            return getUUID();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (tm != null) {
                            if (!TextUtils.isEmpty(tm.getDeviceId())) {
                                return tm.getDeviceId();
                            } else {
                                return getUUID();
                            }
                        } else {
                            return getUUID();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return getUUID();
            }
        }
        return getUUID();
    }

    /**
     * 获取UUID
     */
    public static String getUUID() {
        String uuid;
        UUID id = UUID.randomUUID();
        uuid = id.toString().replace("-", "");
        //为了统一格式对设备的唯一标识进行md5加密，最终生成32位字符串
        return getMD5(uuid, false);
    }

    /**
     * 对特定的内容进行 md5 加密
     * @param message  加密明文
     * @param upperCase  加密以后的字符串是是大写还是小写  true 大写  false 小写
     */
    private static String getMD5(String message, boolean upperCase) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff, upperCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    private static String bytesToHex(byte[] bytes, boolean upperCase) {
        StringBuffer md5str = new StringBuffer();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        if (upperCase) {
            return md5str.toString().toUpperCase();
        }
        return md5str.toString().toLowerCase();
    }

    /**
     * 读取sd卡中保存的设备唯一标识符
     */
    public static String readDeviceID(Context context) {
        File file = getDevicesDir(context);
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Reader in = new BufferedReader(isr);
            int i;
            while ((i = in.read()) > -1) {
                buffer.append((char) i);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 统一处理设备唯一标识 保存的文件的地址
     */
    private static File getDevicesDir(Context context) {
        File cropFile;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cropDir = new File(Environment.getExternalStorageDirectory(), CACHE_IMAGE_DIR);
            if (!cropDir.exists()) {
                cropDir.mkdirs();
            }
            cropFile = new File(cropDir, DEVICES_FILE_NAME);
        } else {
            File cropDir = new File(context.getFilesDir(), CACHE_IMAGE_DIR);
            if (!cropDir.exists()) {
                cropDir.mkdirs();
            }
            cropFile = new File(cropDir, DEVICES_FILE_NAME);
        }
        return cropFile;
    }

    /**
     * 保存内容到SD卡中
     */
    public static void saveDeviceID(String str, Context context) {
        if (TextUtils.isEmpty(str)) return;
        File file = getDevicesDir(context);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取app数据存储deviceId
     */
    public static String getSpDeviceId() {
        return SpUtils.getInstance().getString(GEETOL_DEVICE_ID);
    }

    /**
     * 存储deviceId到app数据
     */
    public static void putSpDeviceId(String deviceId) {
        SpUtils.getInstance().putString(GEETOL_DEVICE_ID, deviceId);
    }
}

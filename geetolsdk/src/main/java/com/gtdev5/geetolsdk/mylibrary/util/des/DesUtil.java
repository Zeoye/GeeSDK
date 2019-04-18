package com.gtdev5.geetolsdk.mylibrary.util.des;

import android.util.Base64;
import android.util.Log;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by ZL on 2019/4/18
 *
 * 后台加解密工具
 */

public class DesUtil {
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    public static final String KEY = "yonggenb";
    public static final String IV = "jingxima";
    private final static String encoding = "utf-8";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            DESKeySpec dks = new DESKeySpec(KEY.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key key = keyFactory.generateSecret(dks);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] bytes = cipher.doFinal(data);
            Log.e("base64", byteToBase64(bytes) + "");
            return byteToBase64(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取编码后的值
     *
     * @param key
     * @param data
     * @return
     */
    public static String decode(String key, String data) {
        return decode(key, String.valueOf(Base64.decode(data, Base64.DEFAULT)));
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @return 解密后的字节数组
     */
    public static byte[] decode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            DESKeySpec dks = new DESKeySpec(KEY.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key key = keyFactory.generateSecret(dks);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] original = cipher.doFinal(data);
            // String originalString = new String(original);
            //return originalString;
            return original;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * byte转Base64字符串
     */
    public static String byteToBase64(byte[] bytes){
        if (bytes == null) {
            return "";
        }
        String strBase64 = "";
        try {
            BASE64Encoder enc = new BASE64Encoder();
            strBase64 = enc.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strBase64;
    }
}

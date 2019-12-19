package com.gtdev5.geetolsdk.mylibrary.feedback.utils;

import android.app.Activity;

import com.gtdev5.geetolsdk.mylibrary.util.PermissionUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

/**
 * Created by ZL on 2019/12/18
 *
 * 图片选择工具类
 */
public class GTMatisseUtil {
    //权限
    public static String[] PICTURE_PERMISSION = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 选择指定数量的照片
     */
    public static void getPhoto(Activity context, int num, int permission_code, int image_code){
        PermissionUtils.checkAndRequestMorePermissions(context, PICTURE_PERMISSION, permission_code, () ->
                Matisse.from(context)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(num)
                .spanCount(4)
                .imageEngine(new GlideEngine()).forResult(image_code));
    }
}

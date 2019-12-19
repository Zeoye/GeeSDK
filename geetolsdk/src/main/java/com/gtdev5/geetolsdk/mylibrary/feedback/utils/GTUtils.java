package com.gtdev5.geetolsdk.mylibrary.feedback.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.graphics.ColorUtils;
import android.util.Base64;

import com.gtdev5.geetolsdk.R;
import com.gyf.barlibrary.ImmersionBar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZL on 2019/12/18
 *
 * 通用工具
 */
public class GTUtils {
    /**
     * 设置沉浸式状态栏
     */
    public static void setImmersionStatusBar(Activity activity) {
        ImmersionBar.with(activity)
                .statusBarColorInt(activity.getResources().getColor(R.color.gt_main_color))
                .init();
        if (ColorUtils.calculateLuminance(activity.getResources().getColor(R.color.gt_main_color)) >= 0.5) {
            ImmersionBar.with(activity)
                    .statusBarDarkFont(true, 0.2f)//状态栏字体深色或亮色
                    .init();
        } else {
            ImmersionBar.with(activity)
                    .statusBarDarkFont(false, 0.2f)//状态栏字体深色或亮色
                    .init();
        }
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     */
    public static String Bitmap2StrByBase64(Bitmap bitmap) {
        String reslut = "";
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                /**
                 * 压缩只对保存有效果bitmap还是原来的大小
                 */
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                int options = 100;
                while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();//重置baos即清空baos
                    //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                    if (options > 5) {
                        options -= 5;//每次都减少10
                    } else {
                        break;
                    }
                }
                // 转换为字节数组
                byte[] byteArray = baos.toByteArray();
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT);
                baos.flush();
                baos.close();
                return reslut;
                // 转换为字符串
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reslut;
    }

    /**
     * 通过uri获取图片并进行压缩
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1)) return null;
        //图片分辨率以480x800为标准
        float hh = 1920f;//这里设置高度为800f
        float ww = 1080f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0) be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;//再进行质量压缩
    }
}

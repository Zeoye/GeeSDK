package com.gtdev5.geetolsdk.mylibrary.util;

import com.gtdev5.geetolsdk.mylibrary.beans.Gds;
import com.gtdev5.geetolsdk.mylibrary.beans.PayType;

/**
 * Info     ： Create by Zeoy
 * Introduce：判断支付类型的工具
 * Date     ： 2019/3/11
 */
public class PayUtil {
    public static final String PAYWAY_ZFB = "[2]";
    public static final String PAYWAY_WECHAT = "[1]";

    /**
     * 检查商品支持的支付方式
     * @param gds   商品对象
     * @return  支持的支付方式
     */
    public static PayType checkType(Gds gds){
        if (gds==null||gds.getPayway()==null){
            return PayType.NO_PAY;
        }
        if (gds.getPayway().contains(PAYWAY_ZFB)&&gds.getPayway().contains(PAYWAY_WECHAT)){
            return PayType.BOTH_ZFB_WECHAT;
        }else if (gds.getPayway().contains(PAYWAY_ZFB)){
            return PayType.ONLY_ZFB;
        }else if (gds.getPayway().contains(PAYWAY_WECHAT)){
            return PayType.ONLY_WECHAT;
        }else {
            return PayType.NO_PAY;
        }
    }
}

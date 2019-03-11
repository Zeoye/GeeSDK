package com.gtdev5.geetolsdk.mylibrary.beans;

/**
 * Info     ： Create by Zeoy
 * Introduce：支付类型枚举
 * Date     ： 2019/3/11
 */
public enum PayType {
    BOTH_ZFB_WECHAT,//支付宝和微信都有
    ONLY_ZFB,//只有支付宝
    ONLY_WECHAT,//只有微信
    NO_PAY//没有支付方式
}

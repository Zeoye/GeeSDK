package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheng
 * PackageName ModelTest
 * 2018/1/22 15:44
 * 数据对接管理类
 */

public class MapUtils {

    private static Context mContext;

    /**
     * 在Application里面初始化，就能全局调用
     *
     * @param context
     */
    public static void init(Context context) {
        if (mContext == null) {
            mContext = context;
        } else {
            return;
        }
    }

    /**
     * 通用Map
     * (无参的方法通用调取)
     *
     * @return
     */
    public static Map<String, String> getCurrencyMap() {
        Map<String, String> map = new HashMap<>();
        map.put("appid", CPResourceUtils.getString("appid"));
        map.put("sign", null);
        map.put("device", MacUtils.getMac(mContext));
        return map;
    }

    /**
     * 获取device
     * (无参的方法通用调取)
     *
     * @return
     */
    public static Map<String, String> getDeviceMap() {
        Map<String, String> map = new HashMap<>();
        map.put("appid", CPResourceUtils.getString("appid"));
        map.put("sign", null);
        map.put("device", CPResourceUtils.getDevice());
        return map;
    }

    /**
     * 通用用户Map
     * (需要登陆用户信息时调取)
     *
     * @return
     */
    public static Map<String, String> getCommonUserMap() {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("user_id", Utils.getUserId());
        map.put("user_key", Utils.getUserKey());
        return map;
    }

    /**
     * 注册
     *
     * @return
     */
    public static Map<String, String> getRegistMap() {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("brand", SystemUtils.getDeviceBrand());
        map.put("model", SystemUtils.getSystemModel());
        map.put("widthpix", SystemUtils.getWith(mContext) + "");
        map.put("heightpix", SystemUtils.getHeight(mContext) + "");
        map.put("vercode", SystemUtils.getSystemVersion());
        map.put("agent", SystemUtils.getChannelInfo(mContext) + "");
        return map;
    }

    /**
     * 版本更新
     *
     * @return
     */
    public static Map<String, String> getNewMap() {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("code", Utils.getVersion(mContext));
        return map;
    }

    /**
     * 意见反馈
     *
     * @param content 意见内容
     * @param phone   联系方式
     * @return
     */
    public static Map<String, String> getFeedBack(String content, String phone) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("content", content);
        map.put("contact", phone);
        return map;
    }

    /**
     * 订单详情
     *
     * @param type   订单类型    1:支付    2:打赏
     * @param pid    商品ID
     * @param amount 打赏订单必填,支付可不填
     * @param pway   支付类型    1:微信    2:支付宝
     * @return
     */
    public static Map<String, String> getOrder(int type, int pid, float amount, int pway) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("type", String.valueOf(type));
        map.put("pid", String.valueOf(pid));
        map.put("amount", String.valueOf(amount));
        map.put("pway", String.valueOf(pway));
        return map;
    }

    /**
     * 添加反馈
     *
     * @param title   标题  不能为空
     * @param descibe 简介
     * @param type    类型
     * @param img     base64图片  多个用，分割
     * @return
     */
    public static Map<String, String> getAddServiceMap(String title, String descibe, String type, String img) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("title", title);
        map.put("describe", descibe);
        map.put("type", type);
        map.put("img", img);
        return map;
    }

    /**
     * 获取反馈列表
     *
     * @param page  页码
     * @param limit 单页条数
     * @return
     */
    public static Map<String, String> getGetServiceMap(int page, int limit) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("page", String.valueOf(page));
        map.put("limit", String.valueOf(limit));
        return map;
    }

    /**
     * 获取反馈详情
     *
     * @param service_id
     * @return
     */
    public static Map<String, String> getServiceDetialsMap(int service_id) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("id", String.valueOf(service_id));
        return map;
    }

    /**
     * 添加反馈回复
     *
     * @param service_id
     * @param repley
     * @param img
     * @return
     */
    public static Map<String, String> getAddRepleyMap(int service_id, String repley, String img) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("service_id", String.valueOf(service_id));
        map.put("desc", repley);
        map.put("img", img);
        return map;
    }

    /**
     * 获取下载链接
     * @param apid
     * @return
     */
    public static Map<String, String> getAppUrlMap(long apid) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("apid", String.valueOf(apid));
        return map;
    }

    /**
     * 获取验证码
     * @param tel 手机号
     * @return
     */
    public static Map<String, String> getVarCode(String tel) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("tel", tel);
        return map;
    }

    /**
     * 用户注册
     * @param tel 手机号
     * @param code 验证码
     * @param pwd 密码
     * @param ckey 验证码接口返回的令牌
     * @return
     */
    public static Map<String, String> userRegister(String tel, String code, String pwd, String ckey) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("tel", tel);
        map.put("code", code);
        map.put("pwd", pwd);
        map.put("ckey", ckey);
        return map;
    }

    /**
     * 用户登陆传参
     * @param name 账号
     * @param pwd 密码
     * @return
     */
    public static Map<String, String> userLogin(String name, String pwd) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("loginname", name);
        map.put("loginpwd", pwd);
        return map;
    }

    /**
     * 忘记密码
     * @param tel 手机号
     * @param code 验证码
     * @param npwd 新密码
     * @param ckey 验证码接口返回的令牌
     * @return
     */
    public static Map<String, String> forgetPwd(String tel, String code, String npwd, String ckey) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("tel", tel);
        map.put("code", code);
        map.put("npwd", npwd);
        map.put("ckey", ckey);
        return map;
    }

    /**
     * 修改密码
     * @param opwd 旧密码
     * @param npwd 新密码
     * @return
     */
    public static Map<String, String> modifyPwd(String opwd, String npwd) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCommonUserMap());
        map.put("opwd", opwd);
        map.put("npwd", npwd);
        return map;
    }

    /**
     * 修改用户头像
     * @param img 用户头像base64字符串
     * @param name 上传文件的名字,必须带上扩展名
     * @return
     */
    public static Map<String, String> setUserHead(String img, String name) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCommonUserMap());
        map.put("img", img);
        map.put("name", name);
        return map;
    }

    /**
     * 获取用户头像
     * @param name 需要获取头像文件的名字
     * @return
     */
    public static Map<String, String> getUserHead(String name) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getCurrencyMap());
        map.put("name", name);
        return map;
    }

    /**
     * 通用Map
     * (无参的方法通用调取)
     *
     * @return
     */
    public static Map<String, String> getReplaceImei() {
        Map<String, String> map = new HashMap<>();
        map.putAll(getDeviceMap());
        map.put("new_imei", MacUtils.getMac(mContext));
        return map;
    }
}

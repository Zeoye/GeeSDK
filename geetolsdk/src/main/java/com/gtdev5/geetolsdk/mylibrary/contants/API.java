package com.gtdev5.geetolsdk.mylibrary.contants;

/**
 * Created by cheng
 * PackageName ModelTest
 * 2018/1/22 13:47
 *         接口管理类
 */

public interface API {

    /**
     * 通用接口
     */
      public static String COMMON_URL = "http://app.wm002.cn/app/";//正式接口
     //public static String COMMON_URL = "http://gtapp.ngrok.80xc.com:82/app/";//测试接口

    /**
     * 设备注册
     */
    public static String REGIST_DEVICE = "reg";

    /**
     *  更新数据
     */
    public static String UPDATE = "update";

    /**
     * 新版检测
     */
    public static String GETNEW = "getnew";

    /**
     *意见反馈
     */
    public static String FEEDBACK = "feedback";

    /**
     * 老订单接口(支付宝，微信)
     */
    public static String ORDER_ONE = "order.one";


    /**
     * 新的支付接口，包括微信和支付宝
     */
    public static String ORDER_OD = "order.od";


    /**
     * 添加服务单
     */
    public static String ADD_SERVICE = "sup.add_service_oss";

    /**
     * 获取服务单
     */
    public static String GET_SERVICE = "sup.get_service";

    /**
     * 获取服务单详情
     */
    public static String GET_SERVICE_DETAILS = "sup.get_service_details_oss";

    /**
     * 添加服务单回复
     */
    public static String ADD_REPLEY = "sup.add_reply_oss";

    /**
     * 结束服务单
     */
    public static String END_SERVICE = "sup.end_service";

    /**
     * 获取app的下载链接
     */
    public static String GET_APPURL = "appurl";

    /**
     * 获取验证码
     */
    public static String GET_VARCODE = "getvarcode";

    /**
     * 手机注册
     */
    public static String USER_REGISTER = "pub_user_reg";

    /**
     * 手机登陆
     */
    public static String USER_LOGIN = "pub_user_login";

    /**
     * 修改密码
     */
    public static String MODIFY_PWD = "pub_user_pwdchange";

    /**
     * 忘记密码
     */
    public static String FORGET_PWD = "pub_forget_pwd";

    /**
     * 设置头像
     */
    public static String SET_HEADING = "pub_set_heading";

    /**
     * 获取头像
     */
    public static String GET_HEADING = "pub_get_heading";

    /**
     * 获取阿里云oss参数
     */
    public static String GET_ALIOSS = "get_ali_oss";

    /**
     * 手机动态码登陆
     * // 2019.11.11新增
     */
    public static String USER_LOGIN_CODE = "sms.userlogin";

    /**
     * 校验登录状态
     * // 2019.11.11新增
     */
    public static String USER_LOGIN_CHECK = "sms.statelogin";

    /**
     * 微信登录
     */
    public static String USER_WECHAT_LOGIN = "pub_wechat_login";
}
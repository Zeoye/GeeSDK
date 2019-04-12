package com.gtdev5.geetolsdk.mylibrary.beans;

import java.io.Serializable;

/**
 * Created by ZL on 2019/4/12
 */

public class LoginInfoBean implements Serializable {
    private boolean issucc;            //调用是否成功
    private String msg;
    private String code;
    private String user_id; // 用户id
    private String ukey; // 用户相关接口需要验证的一个参数

    public boolean isIssucc() {
        return issucc;
    }

    public void setIssucc(boolean issucc) {
        this.issucc = issucc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }
}

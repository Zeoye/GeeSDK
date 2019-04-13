package com.gtdev5.geetolsdk.mylibrary.beans;

import java.io.Serializable;

/**
 * Created by ZL on 2019/4/12
 */

public class LoginInfoBean implements Serializable {
    private boolean issucc;
    private String msg;
    private String code;
    private LoginInfo data;

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

    public LoginInfo getData() {
        return data;
    }

    public void setData(LoginInfo data) {
        this.data = data;
    }
}

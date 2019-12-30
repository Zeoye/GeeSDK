package com.gtdev5.geetolsdk.mylibrary.beans;

import java.io.Serializable;

/**
 * Created by cheng
 * PackageName APP_Lock
 * 2018/1/24 17:25
 */

public class Vip implements Serializable {
    private String viplevel;
    private int count;
    private String time;
    private boolean isout;
    private String viptag;
    /**
     * 用户注册时间
     */
    private String ctime;

    public String getViptag() {
        return viptag;
    }

    public void setViptag(String viptag) {
        this.viptag = viptag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isIsout() {
        return isout;
    }

    public void setIsout(boolean isout) {
        this.isout = isout;
    }

    public String getViplevel() {
        return viplevel;
    }

    public void setViplevel(String viplevel) {
        this.viplevel = viplevel;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}

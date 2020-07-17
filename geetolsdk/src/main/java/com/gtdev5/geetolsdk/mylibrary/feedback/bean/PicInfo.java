package com.gtdev5.geetolsdk.mylibrary.feedback.bean;

import java.io.Serializable;

/**
 * 图片信息
 */
public class PicInfo implements Serializable {
    private String name; // md5名字
    private String path; // 路径

    public PicInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

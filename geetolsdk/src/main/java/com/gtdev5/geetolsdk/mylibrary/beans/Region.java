package com.gtdev5.geetolsdk.mylibrary.beans;

/**
 * 限制地址
 */
public class Region {
    // 地区名字
    private String name;
    // 地区类型，1：省级；2：市级
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

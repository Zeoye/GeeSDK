package com.gtdev5.geetolsdk.mylibrary.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cheng
 * PackageName APP_Lock
 * 2018/1/24 11:00
 *      更新数据类
 */

public class UpdateBean implements Serializable {
    private Vip vip;                //vip信息
    private Boolean issucc;         //调用是否成功
    private String msg;
    private String code;
    private List<Ads> ads;          //广告信息  不同位置的广告信息都在此 Pos表示不同位置  需后台设置
    private List<Gds> gds;          //商品信息（会员）
    private List<Swt> swt;           //开关   需要后台设置
    private Contract contract;        //客服联系信息
    private String hpurl;              //帮助链接
    private Config config;             //微信appid
    private String share_url;          //分享链接
    private List<Region> region;       //限制地址
    private String ip;                 //ip地址

    public UpdateBean(){
    }

    public Boolean getIssucc() {
        return issucc;
    }

    public void setIssucc(Boolean issucc) {
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

    public List<Ads> getAds() {
        return ads;
    }

    public void setAds(List<Ads> ads) {
        this.ads = ads;
    }

    public List<Gds> getGds() {
        return gds;
    }

    public void setGds(List<Gds> gds) {
        this.gds = gds;
    }

    public List<Swt> getSwt() {
        return swt;
    }

    public void setSwt(List<Swt> swt) {
        this.swt = swt;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getHpurl() {
        return hpurl;
    }

    public void setHpurl(String hpurl) {
        this.hpurl = hpurl;
    }

    public Vip getVip() {
        return vip;
    }

    public void setVip(Vip vip) {
        this.vip = vip;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public List<Region> getRegion() {
        return region;
    }

    public void setRegion(List<Region> region) {
        this.region = region;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

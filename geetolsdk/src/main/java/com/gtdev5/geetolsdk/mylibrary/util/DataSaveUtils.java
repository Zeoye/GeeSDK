package com.gtdev5.geetolsdk.mylibrary.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.gtdev5.geetolsdk.mylibrary.beans.Ads;
import com.gtdev5.geetolsdk.mylibrary.beans.Contract;
import com.gtdev5.geetolsdk.mylibrary.beans.Gds;
import com.gtdev5.geetolsdk.mylibrary.beans.Swt;
import com.gtdev5.geetolsdk.mylibrary.beans.UpdateBean;
import com.gtdev5.geetolsdk.mylibrary.beans.Vip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZL on 2019/10/29
 *
 * 数据存储工具
 */

public class DataSaveUtils {
    /**
     * app数据信息
     */
    private static final String APP_DATA = "geetol_app_data";
    private static DataSaveUtils instance;
    private UpdateBean updateBean;
    private Gson gson;

    public static DataSaveUtils getInstance() {
        if (instance == null) {
            synchronized (DataSaveUtils.class) {
                if (instance == null) {
                    instance = new DataSaveUtils();
                }
            }
        }
        return instance;
    }

    private DataSaveUtils() {
        gson = new Gson();
        updateBean = gson.fromJson(SpUtils.getInstance().getString(APP_DATA), UpdateBean.class);
        if (updateBean == null) {
            updateBean = new UpdateBean();
        }
    }

    /**
     * 保存app数据到本地
     */
    public void saveAppData(UpdateBean updateBean) {
        this.updateBean = updateBean;
        SpUtils.getInstance().putString(APP_DATA, gson.toJson(updateBean));
    }

    /**
     * 获取Update的所有信息
     */
    public UpdateBean getUpdate() {
        return updateBean;
    }

    /**
     * 获取所有启动图、banner图信息
     */
    public List<Ads> getAllAds() {
        if (updateBean != null) {
            return updateBean.getAds();
        }
        return null;
    }

    /**
     * 获取启动页数据(开屏广告)
     *
     * @param splashPos 启动图位置
     */
    public Ads getSplashAd(String splashPos) {
        List<Ads> ads = updateBean.getAds();
        if (ads != null && ads.size() > 0) {
            for (Ads ad : ads) {
                if (ad.getPos().equals(splashPos)) {
                    return ad;
                }
            }
        }
        return null;
    }

    /**
     * 获取Banner数据
     *
     * @param bannerPos 轮播图位置(为空时获取所有banner)
     */
    public List<Ads> getBanners(String bannerPos) {
        List<Ads> banners = new ArrayList<>();
        if (updateBean != null) {
            List<Ads> ads = updateBean.getAds();
            if (ads != null && ads.size() > 0) {
                for (Ads ad : ads) {
                    if (ad.getPos().equals(bannerPos) || bannerPos == null) {
                        banners.add(ad);
                    }
                }
            }
        }
        return banners;
    }

    /**
     * 获取所有商品信息
     */
    public List<Gds> getAllGds() {
        if (updateBean != null) {
            return updateBean.getGds();
        }
        return null;
    }

    /**
     * 获取帮助链接
     */
    public String getHelpUrl() {
        if (updateBean != null) {
            return updateBean.getHpurl();
        }
        return null;
    }

    /**
     * 获取客服信息(包含客服类型和客服联系方式)
     */
    public Contract getContract() {
        if (updateBean != null) {
            return updateBean.getContract();
        }
        return null;
    }

    /**
     * 获取客服类型
     */
    public String getContractType() {
        Contract contract = getContract();
        if (contract != null && !TextUtils.isEmpty(contract.getTxt())) {
            return contract.getTxt();
        }
        return null;
    }

    /**
     * 获取客服号码
     */
    public String getContractNum() {
        Contract contract = getContract();
        if (contract != null && !TextUtils.isEmpty(contract.getNum())) {
            return contract.getNum();
        }
        return null;
    }

    /**
     * 获取VIP信息
     */
    public Vip getVip() {
        if (updateBean != null) {
            return updateBean.getVip();
        }
        return null;
    }

    /**
     * 是否是vip
     */
    public boolean isVip() {
        Vip vip = getVip();
        return vip != null && !vip.isIsout();
    }

    /**
     * 获取所有开关信息
     */
    public List<Swt> getSwt() {
        if (updateBean != null) {
            return updateBean.getSwt();
        }
        return null;
    }

    /**
     * 获取微信id
     */
    public String getWxId() {
        if (updateBean != null) {
            if (updateBean.getConfig() != null) {
                if (!TextUtils.isEmpty(updateBean.getConfig().getWxid())) {
                    return updateBean.getConfig().getWxid();
                }
            }
        }
        return null;
    }

    /**
     * 判断用户是否是老用户(用于截图系列新旧用户不同页面逻辑判断)
     * @param time 判断新旧用户的时间，格式yyyy-MM-dd HH:mm:ss
     */
    public boolean isOldUser(String time) {
        SimpleDateFormat CurrentTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Vip vip = getVip();
        if (vip != null) {
            try {
                Date oldTime = CurrentTime.parse(vip.getCtime());
                Date currentTime = CurrentTime.parse(time);
                Log.e("Tag",
                        "oldTime:" + oldTime.getTime() + ";currentTime:" + currentTime.getTime());
                return oldTime.getTime() <= currentTime.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

package com.gtdev5.geetolsdk.mylibrary.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.beans.ResultBean;
import com.gtdev5.geetolsdk.mylibrary.beans.UpdateBean;
import com.gtdev5.geetolsdk.mylibrary.callback.BaseCallback;
import com.gtdev5.geetolsdk.mylibrary.contants.Contants;
import com.gtdev5.geetolsdk.mylibrary.http.HttpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.DeviceUtils;
import com.gtdev5.geetolsdk.mylibrary.util.PermissionUtils;
import com.gtdev5.geetolsdk.mylibrary.util.SpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.ToastUtils;
import com.gtdev5.geetolsdk.mylibrary.util.Utils;
import com.gtdev5.geetolsdk.mylibrary.widget.CenterDialog;
import com.gtdev5.geetolsdk.mylibrary.widget.OnDialogClickListener;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZL on 2019/10/30
 *
 * 启动页基类
 */

public abstract class BaseLaunchActivity extends AppCompatActivity {
    protected Context mContext;
    protected BaseLaunchActivity mActivity;

    private int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
    public static final int RESULT_ACTION_SETTING = 1;
    private boolean isFirstRegister; // 是否首次注册

    /**
     * 获取读写权限
     */
    public String[] getPermissions28() {
        return new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission
                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    /**
     * 获取读写权限
     */
    public String[] getPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    private String[] Permissions;

    /**
     * 资源id
     */
    protected abstract int getLayoutID();

    /**
     * 逻辑跳转(例如跳转到首页)
     */
    protected abstract void jumpToNext();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(getLayoutID());
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        isFirstRegister = SpUtils.getInstance().getBoolean(Contants.FIRST_REGISTER, true);
        Permissions = Build.VERSION.SDK_INT <= 28 ? getPermissions28() : getPermissions();
        PermissionUtils.checkAndRequestMorePermissions(mActivity, Permissions,
                RESULT_ACTION_USAGE_ACCESS_SETTINGS, this::bindDevice);
    }

    /***
     * 绑定device数据
     */
    private void bindDevice() {
        if (TextUtils.isEmpty(DeviceUtils.getSpDeviceId()) && TextUtils.isEmpty(DeviceUtils.readDeviceID(mContext))) {
            // app和sd卡都没有，都存一份
            String imei = DeviceUtils.getIMEI(this);
            DeviceUtils.saveDeviceID(imei, mContext);
            DeviceUtils.putSpDeviceId(imei);
        } else if (TextUtils.isEmpty(DeviceUtils.getSpDeviceId()) && !TextUtils.isEmpty(DeviceUtils.readDeviceID(mContext))) {
            // sd卡有，app没有，则存一份到app
            DeviceUtils.putSpDeviceId(DeviceUtils.readDeviceID(mContext));
        } else {
            // app有，sd卡没有，则存一份到sd卡
            DeviceUtils.saveDeviceID(DeviceUtils.getSpDeviceId(), mContext);
        }
        registerId();
    }

    /**
     * 注册设备id
     */
    private void registerId() {
        if (isFirstRegister) {
            if (Utils.isNetworkAvailable(this)) {
                HttpUtils.getInstance().postRegister(new BaseCallback<ResultBean>() {
                    @Override
                    public void onRequestBefore() {
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        ToastUtils.showShortToast("网络异常！请开启网络后重新打开APP");
                        SpUtils.getInstance().putBoolean(Contants.FIRST_REGISTER, true);
                    }

                    @Override
                    public void onSuccess(Response response, ResultBean o) {
                        if (o != null) {
                            if (o.isIssucc()) {
                                // 注册成功，调取App数据接口
                                getUpdateInfo();
                                SpUtils.getInstance().putBoolean(Contants.FIRST_REGISTER, false);
                            } else {
                                SpUtils.getInstance().putBoolean(Contants.FIRST_REGISTER, false);
                                // 注册失败，弹出提示
                                if (!TextUtils.isEmpty(o.getMsg())) {
                                    ToastUtils.showShortToast(o.getMsg());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response response, int errorCode, Exception e) {
                        SpUtils.getInstance().putBoolean(Contants.FIRST_REGISTER, true);
                        ToastUtils.showShortToast("网络异常！请开启网络后重新打开APP");
                    }
                });
            } else {
                // 没有网络请求
                ToastUtils.showShortToast("网络异常！请开启网络后重新打开APP");
            }
        } else {
            getUpdateInfo();
        }
    }

    /**
     * 获取App数据信息
     */
    private void getUpdateInfo() {
        if (Utils.isNetworkAvailable(this)) {
            HttpUtils.getInstance().postUpdate(new BaseCallback<UpdateBean>() {
                @Override
                public void onRequestBefore() {
                }

                @Override
                public void onFailure(Request request, Exception e) {
                }

                @Override
                public void onSuccess(Response response, UpdateBean o) {
                    if (o != null && o.getIssucc()) {
                        // 数据获取成功跳转到下个页面
                        jumpToNext();
                    }
                }

                @Override
                public void onError(Response response, int errorCode, Exception e) {
                }
            });
        } else {
            // 没有网络，但注册过，直接跳转到下个页面，并且做无网络提示
            ToastUtils.showShortToast("网络异常！请检查网络是否开启");
            jumpToNext();
        }
        checkLogin();
    }

    /**
     * 校验登陆
     */
    private void checkLogin() {
        if (Utils.isNetworkAvailable(this)) {
            if (!TextUtils.isEmpty(Utils.getUserId()) && !Utils.getUserId().equals("0")) {
                // 登录过
                HttpUtils.getInstance().checkLogin(new BaseCallback<ResultBean>() {
                    @Override
                    public void onRequestBefore() {}

                    @Override
                    public void onFailure(Request request, Exception e) {}

                    @Override
                    public void onSuccess(Response response, ResultBean o) {
                        if (o != null) {
                            if (o.isIssucc()) {
                                Log.e("校验登录:", "已经登录过");
                            } else {
                                Log.e("校验登录:", "已在别机登录，本机下线");
                            }
                        }
                    }

                    @Override
                    public void onError(Response response, int errorCode, Exception e) {}
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            PermissionUtils.onRequestMorePermissionsResult(mContext, permissions,
                    new PermissionUtils.PermissionCheckCallBack() {
                        @Override
                        public void onHasPermission() {
                            bindDevice();
                        }

                        @Override
                        public void onUserHasAlreadyTurnedDown(String... permission) {
                            ShowTipDialog("温馨提示",
                                    "授予权限能使数据绑定手机哦，点击确定继续授权",
                                    "确定", new OnDialogClickListener() {
                                        @Override
                                        public void OnDialogOK() {
                                            PermissionUtils.requestMorePermissions(mActivity,
                                                    permissions, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
                                        }

                                        @Override
                                        public void OnDialogExit() {
                                            // 退出APP
                                            finish();
                                        }
                                    });
                        }

                        @Override
                        public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                            ShowTipDialog("温馨提示",
                                    "授予权限才能使用软件喔，请到设置中允许权限",
                                    "确定", new OnDialogClickListener() {
                                        @Override
                                        public void OnDialogOK() {
                                            Intent mIntent = new Intent();
                                            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                            mIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(mIntent, RESULT_ACTION_SETTING);
                                        }

                                        @Override
                                        public void OnDialogExit() {
                                            // 退出APP
                                            finish();
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACTION_SETTING) {
            PermissionUtils.checkAndRequestMorePermissions(mActivity, Permissions,
                    RESULT_ACTION_USAGE_ACCESS_SETTINGS, this::bindDevice);
        }
    }

    /**
     * 提示弹框
     */
    public void ShowTipDialog(String name, String content, String btnText, OnDialogClickListener listener) {
        int[] ids = new int[]{
                R.id.dialog_bt_dis,
                R.id.dialog_bt_ok
        };
        CenterDialog dialog = new CenterDialog(mActivity, R.layout.dialog_show_tip, ids, false);
        dialog.setOnCenterClickListener((dial, view) -> {
            if (view.getId() == R.id.dialog_bt_ok) {
                listener.OnDialogOK();
                dialog.dismiss();
            }
            if (view.getId() == R.id.dialog_bt_dis) {
                dialog.dismiss();
                listener.OnDialogExit();
            }
        });
        dialog.show();
        dialog.setText(R.id.dialog_tv_title, name);
        dialog.setText(R.id.dialog_tv_text, content);
        dialog.setText(R.id.dialog_bt_ok, btnText);
    }
}

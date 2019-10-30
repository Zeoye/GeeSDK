package com.gtdev5.geetolsdk.mylibrary.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.beans.ResultBean;
import com.gtdev5.geetolsdk.mylibrary.beans.UpdateBean;
import com.gtdev5.geetolsdk.mylibrary.callback.BaseCallback;
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
    public static final String IS_FIRST_REGISTER = "is_first_register"; // 是否首次注册
    protected Context mContext;
    protected BaseLaunchActivity mActivity;

    private boolean isFirstReg; //是否第一次注册
    private int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;

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
        isFirstReg = SpUtils.getInstance().getBoolean(IS_FIRST_REGISTER, true);//是否是第一次注册
        if (!TextUtils.isEmpty(DeviceUtils.getSpDeviceId())) {
            // 若app数据里存储过deviceId
            if (!TextUtils.isEmpty(DeviceUtils.readDeviceID(this)) &&
                    !DeviceUtils.readDeviceID(this).equals(DeviceUtils.getSpDeviceId())) {
                // 若sd卡里存储过deviceId并且与app数据里的不同，则替换sd卡中的deviceId
                DeviceUtils.saveDeviceID(DeviceUtils.getSpDeviceId(), mContext);
            }
        } else {
            // app没有存储过deviceId(第一次启动)
            if (!TextUtils.isEmpty(DeviceUtils.readDeviceID(this))) {
                // 若sd卡里存储过deviceId，则app数据也保存一份
                DeviceUtils.putSpDeviceId(DeviceUtils.readDeviceID(this));
            }
        }
        if (android.os.Build.VERSION.SDK_INT <= 28) {
            // 获取imei作为deviceId
            PermissionUtils.checkAndRequestMorePermissions(mActivity, getPermissions28(),
                    RESULT_ACTION_USAGE_ACCESS_SETTINGS, () -> {
                        // 获取到了状态码权限
                        registerIdBeforeQ();
                    });
        } else {
            // 获取uuid作为deviceId
            PermissionUtils.checkAndRequestMorePermissions(mActivity, getPermissions(),
                    RESULT_ACTION_USAGE_ACCESS_SETTINGS, () -> {
                registerIdAfterQ();
            });
        }
    }

    /**
     * AndroidQ之前注册逻辑
     */
    private void registerIdBeforeQ() {
        // 获取到了状态码权限
        String deviceId = DeviceUtils.getIMEI(mActivity);
        if (TextUtils.isEmpty(deviceId)) {
            // 若此时还是获取不到imei，则用随机生成的uuid
            deviceId = DeviceUtils.getUUID();
        }
        DeviceUtils.putSpDeviceId(deviceId);
        DeviceUtils.saveDeviceID(deviceId, mContext);
        registerId();
    }

    /**
     * AndroidQ之后的注册逻辑
     */
    private void registerIdAfterQ() {
        if (TextUtils.isEmpty(DeviceUtils.getSpDeviceId()) && TextUtils.isEmpty(DeviceUtils.readDeviceID(this))) {
            DeviceUtils.putSpDeviceId(DeviceUtils.getUUID());
            DeviceUtils.saveDeviceID(DeviceUtils.getUUID(), mContext);
        }
        registerId();
    }

    /**
     * 注册设备id
     */
    private void registerId() {
        if (isFirstReg) {
            if (Utils.isNetworkAvailable(this)) {
                HttpUtils.getInstance().postRegister(new BaseCallback<ResultBean>() {
                    @Override
                    public void onRequestBefore() {}

                    @Override
                    public void onFailure(Request request, Exception e) {
                        SpUtils.getInstance().putBoolean(IS_FIRST_REGISTER, true);
                    }

                    @Override
                    public void onSuccess(Response response, ResultBean o) {
                        if (o != null && o.isIssucc()) {
                            SpUtils.getInstance().putBoolean(IS_FIRST_REGISTER, false);
                            // 注册成功，调取App数据接口
                            getUpdateInfo();
                        }
                    }

                    @Override
                    public void onError(Response response, int errorCode, Exception e) {
                        SpUtils.getInstance().putBoolean(IS_FIRST_REGISTER, true);
                    }
                });
            } else {
                // 首次进入，并且没有网络请求
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
                public void onRequestBefore() {}

                @Override
                public void onFailure(Request request, Exception e) {}

                @Override
                public void onSuccess(Response response, UpdateBean o) {
                    if (o != null && o.getIssucc()) {
                        // 数据获取成功跳转到下个页面
                        jumpToNext();
                    }
                }

                @Override
                public void onError(Response response, int errorCode, Exception e) {}
            });
        } else {
            // 没有网络，但注册过，直接跳转到下个页面，并且做无网络提示
            ToastUtils.showShortToast("网络异常！请检查网络是否开启");
            jumpToNext();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            String[] curPermissions;
            if (android.os.Build.VERSION.SDK_INT <= 28) {
                curPermissions = getPermissions28();
            } else {
                curPermissions = getPermissions();
            }
            PermissionUtils.onRequestMorePermissionsResult(mContext, curPermissions,
                    new PermissionUtils.PermissionCheckCallBack() {
                @Override
                public void onHasPermission() {
                    if (android.os.Build.VERSION.SDK_INT <= 28) {
                        registerIdBeforeQ();
                    } else {
                        registerIdAfterQ();
                    }
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
                                    Intent intent = new Intent();
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package", getPackageName(),
                                            null));
                                    startActivityForResult(intent, 1);
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
        if (requestCode == 1) {
            String[] curPermissions;
            if (android.os.Build.VERSION.SDK_INT <= 28) {
                curPermissions = getPermissions28();
            } else {
                curPermissions = getPermissions();
            }
            PermissionUtils.checkAndRequestMorePermissions(mActivity, curPermissions,
                    RESULT_ACTION_USAGE_ACCESS_SETTINGS, () -> {
                if (android.os.Build.VERSION.SDK_INT <= 28) {
                    registerIdBeforeQ();
                } else {
                    registerIdAfterQ();
                }
            });
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

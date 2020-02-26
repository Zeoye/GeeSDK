package com.gtdev5.geetolsdk.mylibrary.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gtdev5.geetolsdk.R;
import com.gtdev5.geetolsdk.mylibrary.beans.GetNewBean;
import com.gtdev5.geetolsdk.mylibrary.util.GTDownloadUtils;
import com.gtdev5.geetolsdk.mylibrary.util.ToastUtils;
import com.gtdev5.geetolsdk.mylibrary.util.Utils;

import java.io.File;

/**
 * 软件下载更新弹框
 */
public class GTDownloadApkDialog extends BaseDialog {
    private ImageView mCancelImage, mDownLoadImage;
    private TextView mDownLoadText, mUpdateInfoText, mCancelText, mVersionText;
    private NumberProgressBar mNumberProgressBar;
    private GetNewBean mGetNewBean;
    private Context mContext;
    private File file;
    private int currentProgress;
    private String mAuthority;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1002:
                    ToastUtils.showShortToast("下载完成");
                    if (GTDownloadApkDialog.this != null) {
                        GTDownloadApkDialog.this.dismiss();
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.putExtra("name", "");
                        intent.addCategory("android.intent.category.DEFAULT");
                        Uri data;
                        if (Build.VERSION.SDK_INT >= 24) {
                            // 临时允许
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            data = FileProvider.getUriForFile(mContext, mAuthority, file);
                        } else {
                            data = Uri.fromFile(file);
                        }
                        intent.setDataAndType(data, "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        Log.e("安装失败", e.toString());
                        if (!TextUtils.isEmpty(mGetNewBean.getDownurl())) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(mGetNewBean.getDownurl()));
                            mContext.startActivity(intent);
                        }
                    }
                    break;
                case 1003:
                    if (mNumberProgressBar != null) {
                        mNumberProgressBar.setProgress(currentProgress);
                    }
                    break;
                case 1004:
                    ToastUtils.showShortToast("下载失败，打开浏览器进行下载更新");
                    if (GTDownloadApkDialog.this != null) {
                        GTDownloadApkDialog.this.dismiss();
                    }
                    break;
            }
        }
    };

    public GTDownloadApkDialog(@NonNull Context context, GetNewBean bean, String authority) {
        super(context);
        this.mContext = context;
        this.mGetNewBean = bean;
        this.mAuthority = authority;
        setCancelable(false);
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        mCancelImage = findViewById(R.id.iv_cancel);
        mCancelText = findViewById(R.id.tv_cancel);
        mDownLoadText = findViewById(R.id.tv_update);
        mDownLoadImage = findViewById(R.id.iv_update);
        mUpdateInfoText = findViewById(R.id.tv_update_info);
        mVersionText = findViewById(R.id.tv_version);
        mNumberProgressBar = findViewById(R.id.number_progressBar);
        if (mGetNewBean != null) {
            if (!TextUtils.isEmpty(mGetNewBean.getLog())) {
                mUpdateInfoText.setText(mGetNewBean.getLog());
            } else {
                mUpdateInfoText.setText("多处功能优化，体验更流畅，服务更稳定，马上更新吧！");
            }
            if (!TextUtils.isEmpty(mGetNewBean.getVername())) {
                mVersionText.setVisibility(View.VISIBLE);
                mVersionText.setText("v" + mGetNewBean.getVername());
            } else {
                mVersionText.setVisibility(View.GONE);
            }
        } else {
            mVersionText.setText("v3.0.0");
            mUpdateInfoText.setText("多处功能优化，体验更流畅，服务更稳定，马上更新吧！");
        }
        mCancelText.setOnClickListener(v -> {
            if (mDownLoadText.getVisibility() == View.GONE && mDownLoadImage.getVisibility() == View.GONE) {
                ToastUtils.showShortToast("正在下载更新中，无法关闭");
            } else {
                dismiss();
            }
        });
        mCancelImage.setOnClickListener(v -> {
            if (mDownLoadText.getVisibility() == View.GONE && mDownLoadImage.getVisibility() == View.GONE) {
                ToastUtils.showShortToast("正在下载更新中，无法关闭");
            } else {
                dismiss();
            }
        });
        mDownLoadImage.setOnClickListener(v -> updateApp());
        mDownLoadText.setOnClickListener(v -> updateApp());
    }

    /**
     * 更新应用
     */
    private void updateApp() {
        //检查网络是否可用
        if (Utils.isNetworkAvailable(mContext)) {
            mDownLoadText.setVisibility(View.GONE);
            mDownLoadImage.setVisibility(View.GONE);
            mNumberProgressBar.setVisibility(View.VISIBLE);
            //下载更新包
            GTDownloadUtils.get().download(mGetNewBean.getDownurl(), "Update", new GTDownloadUtils.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File files) {
                    //下载成功
                    file = files;
                    handler.sendEmptyMessage(1002);
                }

                @Override
                public void onDownloading(int progress) {
                    //在下载
                    handler.sendEmptyMessage(1003);
                    currentProgress = progress;
                }

                @Override
                public void onDownloadFailed() {
                    //下载失败
                    handler.sendEmptyMessage(1004);
                    if (!TextUtils.isEmpty(mGetNewBean.getDownurl())) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mGetNewBean.getDownurl()));
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            ToastUtils.showShortToast("当前网络不可用，请检查网络");
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.gt_dialog_download;
    }
}

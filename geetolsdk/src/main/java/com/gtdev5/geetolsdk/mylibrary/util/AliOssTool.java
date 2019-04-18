package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.gtdev5.geetolsdk.mylibrary.beans.AliOssBean;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by ZL on 2019/4/18
 */

public class AliOssTool {
    private static final int STATE_SUCCESS = 101; // 操作成功
    private static final int STATE_FAIL = 102; // 操作失败
    private static AliOssTool aliOssTool;
    private static AliOssBean aliOssBean;
    OSS mOss;
    private Context mContext;
    private OssCallBack mOssCallBack;
    private MyHandler myHandler = new MyHandler();
    private ProgressCallBack mProgressCallBack;

    private AliOssTool(Context context) {
        this.mContext = context;
        try {
            if (aliOssBean == null) {
                aliOssBean = Utils.getAliOssParam();
            }
            OSSCredentialProvider provider = new OSSPlainTextAKSKCredentialProvider(aliOssBean.getAccessKeyId(),
                    aliOssBean.getAccessKeySecret());
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            mOss = new OSSClient(mContext, aliOssBean.getEndpoint(), provider, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AliOssTool getInstance(Context context) {
        aliOssBean = Utils.getAliOssParam();
        if (aliOssTool == null) {
            synchronized (AliOssTool.class) {
                if (aliOssTool == null) {
                    aliOssTool = new AliOssTool(context);
                }
            }
        }
        return aliOssTool;
    }

    /**
     * 发送message
     * @param what 类型
     * @param o 值
     */
    private void sendMessage(int what, Object o) {
        Message msg = myHandler.obtainMessage();
        msg.what = what;
        msg.obj = o;
        myHandler.sendMessage(msg);
    }

    /**
     * 下载文件
     * @param name 要下载的文件名称
     * @param savePath 要保存的文件地址
     */
    public void downLoadFile(String name, String savePath) {
        downLoadFile(name, savePath, "JPEG");
    }

    /**
     * 下载文件
     * @param name 要下载的文件名称
     * @param savePath 要保存的文件地址
     * @param suffix 要保存的文件后缀
     */
    public void downLoadFile(String name, String savePath, String suffix) {
        GetObjectRequest request = new GetObjectRequest(aliOssBean.getBucketName(), name);
        // 异步上传，可以设置进度回调
        request.setProgressListener((request1, currentSize, totalSize) -> {
            // 进度条回调
            if (mProgressCallBack != null) {
                double progress = currentSize * 1.0 / totalSize * 100.f;
                mProgressCallBack.onProgressCallBack(progress);
            }
        });
        OSSAsyncTask task = mOss.asyncGetObject(request, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                InputStream inputStream = result.getObjectContent();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (saveBitmap2File(bitmap, name, savePath, suffix)) {
                    sendMessage(STATE_SUCCESS, name);
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                sendMessage(STATE_FAIL, name);
                if (clientException != null) {
                    // 本地异常如网络异常等
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    /**
     * 保存bitmap到本地
     * @param bitmap 图片源
     * @param name 图片名称
     * @param path 要保存的地址
     * @param suffix 后缀名
     * @return
     */
    public boolean saveBitmap2File(Bitmap bitmap, String name, String path, String suffix) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (suffix.equals("PNG") || suffix.equals("png")) {
            format = Bitmap.CompressFormat.PNG;
        }
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(path + name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap.compress(format, quality, stream);
    }

    /**
     * 上传文件
     * @param name 名称
     * @param path 地址
     */
    public void upLoadFile(String name, String path) {
        PutObjectRequest request = new PutObjectRequest(aliOssBean.getBucketName(), name, path);
        // 异步上传，可以设置进度回调
        request.setProgressCallback((request1, currentSize, totalSize) -> {
            // 进度条回调
            if (mProgressCallBack != null) {
                double progress = currentSize * 1.0 / totalSize * 100.f;
                mProgressCallBack.onProgressCallBack(progress);
            }
        });
        @SuppressWarnings("rawtypes")
        OSSAsyncTask task = mOss.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                sendMessage(STATE_SUCCESS, path);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                sendMessage(STATE_FAIL, path);
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    /**
     * 删除文件
     * @param name 要删除的名字
     */
    public void deleteFile(String name) {
        DeleteObjectRequest request = new DeleteObjectRequest(aliOssBean.getBucketName(), name);
        // 异步删除
        OSSAsyncTask task = mOss.asyncDeleteObject(request, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                sendMessage(STATE_SUCCESS, name);
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientException, ServiceException serviceException) {
                sendMessage(STATE_FAIL, name);
                if (clientException != null) {
                    // 本地异常，如网络异常等。
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    public void setOssCallBack(OssCallBack ossCallBack) {
        this.mOssCallBack = ossCallBack;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            switch (msg.what) {
                case STATE_SUCCESS:
                    // 操作成功
                    mOssCallBack.onSuccess(s);
                    break;
                case STATE_FAIL:
                    // 操作失败
                    mOssCallBack.onFailure(s);
                    break;
            }
        }
    }

    public interface OssCallBack {
        void onSuccess(String s);
        void onFailure(String s);
    }

    public ProgressCallBack getProgressCallBack() {
        return mProgressCallBack;
    }

    public void setProgressCallback(ProgressCallBack progressCallback) {
        this.mProgressCallBack = progressCallback;
    }

    public interface ProgressCallBack {
        void onProgressCallBack(double progress);
    }
}

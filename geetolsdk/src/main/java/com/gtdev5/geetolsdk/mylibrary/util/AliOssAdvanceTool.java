package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
 * Created by ZL on 2019/4/23
 *
 * 阿里oss文件操作工具升级版
 */

public class AliOssAdvanceTool {
    private static AliOssAdvanceTool aliOssTool;
    private static AliOssBean aliOssBean;
    OSS mOss;
    private Context mContext;

    public AliOssAdvanceTool(Context context) {
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

    public static AliOssAdvanceTool getInstance(Context context) {
        aliOssBean = Utils.getAliOssParam();
        if (aliOssTool == null) {
            synchronized (AliOssAdvanceTool.class) {
                if (aliOssTool == null) {
                    aliOssTool = new AliOssAdvanceTool(context);
                }
            }
        }
        return aliOssTool;
    }

    /**
     * 保存bitmap到本地文件夹
     * @param bitmap 图片源
     * @param name 图片名称
     * @param path 要保存的路径
     */
    public boolean saveBitmap2File(Bitmap bitmap, String name, String path) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (name.contains("png") || name.contains("PNG")) {
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
     * 下载文件
     * @param name 文件名
     * @param path 保存路径
     * @param callBack 下载监听回调
     */
    public void downLoadFile(String name, String path, OssCallBack callBack) {
        downLoadFile(null, aliOssBean.getBucketName(), name, path, null, callBack);
    }

    /**
     * 下载文件
     * @param bucketName 仓库地址
     * @param name 文件名
     * @param path 保存路径
     * @param callback 下载监听回调
     */
    public void downLoadFile(String bucketName, String name, String path, OssCallBack callback) {
        downLoadFile(null, bucketName, name, path, null, callback);
    }

    /**
     * 下载文件
     * @param bucketName 仓库地址
     * @param name 文件名
     * @param path 保存路径
     * @param progressCallBack 下载进度回调
     * @param callback 下载监听回调
     */
    public void downLoadFile(String bucketName, String name, String path, ProgressCallBack progressCallBack, OssCallBack callback) {
        downLoadFile(null, bucketName, name, path, progressCallBack, callback);
    }

    /**
     * 下载文件
     * @param size 文件大小
     * @param bucketName 仓库地址
     * @param name 文件名
     * @param path 保存路径
     * @param callback 下载监听回调
     */
    public void downLoadFile(String size, String bucketName, String name, String path, OssCallBack callback) {
        downLoadFile(size, bucketName, name, path, null, callback);
    }

    /**
     * 下载文件
     * @param size 文件大小
     * @param bucketName 仓库地址
     * @param name 文件名
     * @param path 保存路径
     * @param progressCallBack 下载进度回调
     * @param callback 下载监听回调
     */
    public void downLoadFile(String size, String bucketName, String name, String path, ProgressCallBack progressCallBack, OssCallBack callback) {
        GetObjectRequest request = new GetObjectRequest(bucketName, name);
        if (!TextUtils.isEmpty(size)) {
            request.setxOssProcess(size);
        }
        if (progressCallBack != null) {
            // 异步下载，可以设置进度回调
            request.setProgressListener((request1, currentSize, totalSize) -> {
                double progress = currentSize * 1.0 / totalSize * 100.f;
                new Handler(Looper.getMainLooper()).post(() ->  progressCallBack.onProgressCallBack(progress));
            });
        }
        if (callback != null) {
            OSSAsyncTask task = mOss.asyncGetObject(request, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
                @Override
                public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                    InputStream inputStream = result.getObjectContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (saveBitmap2File(bitmap, name, path)) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(name));
                    }
                }

                @Override
                public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(name));
                    onError(clientException, serviceException);
                }
            });
        }
    }

    /**
     * 上传文件
     * @param name MD5文件名(小写)
     * @param path 文件源路径
     * @param ossCallBack 上传回调
     */
    public void uploadFile(String name, String path, OssCallBack ossCallBack) {
        uploadFile(aliOssBean.getBucketName(), name, path, null, ossCallBack);
    }

    /**
     * 上传文件
     * @param bucketName 仓库地址
     * @param name MD5文件名(小写)
     * @param path 文件源路径
     * @param progressCallBack 下载进度回调
     * @param callBack 上传回调
     */
    public void uploadFile(String bucketName, String name, String path, ProgressCallBack progressCallBack, OssCallBack callBack) {
        PutObjectRequest request = new PutObjectRequest(bucketName, name, path);
        if (progressCallBack != null) {
            // 异步下载，可以设置进度回调
            request.setProgressCallback((request1, currentSize, totalSize) -> {
                double progress = currentSize * 1.0 / totalSize * 100.f;
                new Handler(Looper.getMainLooper()).post(() ->  progressCallBack.onProgressCallBack(progress));
            });
        }
        if (callBack != null) {
            OSSAsyncTask task = mOss.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    new Handler(Looper.getMainLooper()).post(() -> callBack.onSuccess(path));
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    new Handler(Looper.getMainLooper()).post(() -> callBack.onFailure(path));
                    onError(clientException, serviceException);
                }
            });
        }
    }

    /**
     * 删除文件
     * @param name 文件名
     * @param callBack 删除回调
     */
    public void deleteFile(String name, OssCallBack callBack) {
        deleteFile(aliOssBean.getBucketName(), name, callBack);
    }

    /**
     * 删除文件
     * @param bucketName 仓库地址
     * @param name 文件名
     * @param callback 删除回调
     */
    public void deleteFile(String bucketName, String name, OssCallBack callback) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, name);
        if (callback != null) {
            OSSAsyncTask task = mOss.asyncDeleteObject(request, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
                @Override
                public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(name));
                }

                @Override
                public void onFailure(DeleteObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(name));
                    onError(clientException, serviceException);
                }
            });
        }
    }

    /**
     * 错误信息打印
     */
    private void onError(ClientException clientException, ServiceException serviceException) {
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

    public interface OssCallBack {
        void onSuccess(String s);
        void onFailure(String s);
    }

    public interface ProgressCallBack {
        void onProgressCallBack(double progress);
    }
}

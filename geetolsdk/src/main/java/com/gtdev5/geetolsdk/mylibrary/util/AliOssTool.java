package com.gtdev5.geetolsdk.mylibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * Created by ZL on 2019/4/18
 *
 * 阿里云文件操作工具
 */

public class AliOssTool {
    private static AliOssTool aliOssTool;
    private static AliOssBean aliOssBean;
    OSS mOss;
    private Context mContext;
    private ProgressCallBack mProgressCallBack;

    public AliOssTool(Context context) {
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
     * 下载文件
     * @param name 要下载的文件名称
     * @param savePath 要保存的文件地址
     */
    public void downLoadFile(String size, String name, String savePath, OssCallBack callBack) {
        downLoadFile(size, aliOssBean.getBucketName(), name, savePath, "JPEG", callBack);
    }

    /**
     * 下载文件
     * @param size 需要缩放的图片大小
     * @param bucketName 设置仓库地址
     * @param name 要下载的文件名称
     * @param savePath 要保存的文件地址
     * @param suffix 要保存的文件后缀
     * 参数参考：https://help.aliyun.com/document_detail/44688.html?spm=a2c4g.11186623.2.13.685d3331OwJyJD#concept-hxj-c4n-vdb
     */
    public void downLoadFile(String size, String bucketName, String name, String savePath, String suffix, OssCallBack ossCallBack) {
        GetObjectRequest request = new GetObjectRequest(bucketName, name);
        if (!TextUtils.isEmpty(size)) {
            request.setxOssProcess(size);
        }
        // 异步下载，可以设置进度回调
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
                    if (ossCallBack != null) {
                        ossCallBack.onSuccess(name);
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (ossCallBack != null) {
                    ossCallBack.onFailure(name);
                }
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
     * 文件上传
     * @param name 名称
     * @param path 地址
     */
    public void upLoadFile(String name, String path, OssCallBack callBack) {
        upLoadFile(aliOssBean.getBucketName(), name, path, callBack);
    }

    /**
     * 上传文件
     * @param bucketName 仓库地址
     * @param name 名称
     * @param path 地址
     */
    public void upLoadFile(String bucketName, String name, String path, OssCallBack callBack) {
        PutObjectRequest request = new PutObjectRequest(bucketName, name, path);
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
                if (callBack != null) {
                    callBack.onSuccess(path);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (callBack != null) {
                    callBack.onFailure(path);
                }
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
    public void deleteFile(String name, OssCallBack ossCallBack) {
        deleteFile(aliOssBean.getBucketName(), name, ossCallBack);
    }

    /**
     * 删除文件
     * @param bucketName 仓库地址
     * @param name 要删除的名字
     */
    public void deleteFile(String bucketName, String name, OssCallBack ossCallBack) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, name);
        // 异步删除
        OSSAsyncTask task = mOss.asyncDeleteObject(request, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                if (ossCallBack != null) {
                    ossCallBack.onSuccess(name);
                }
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (ossCallBack != null) {
                    ossCallBack.onFailure(name);
                }
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

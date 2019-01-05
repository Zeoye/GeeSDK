package com.gtdev5.geetolsdk.mylibrary.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.gtdev5.geetolsdk.mylibrary.callback.BaseCallback;
import com.gtdev5.geetolsdk.mylibrary.callback.DataCallBack;
import com.gtdev5.geetolsdk.mylibrary.contants.API;
import com.gtdev5.geetolsdk.mylibrary.contants.Contants;
import com.gtdev5.geetolsdk.mylibrary.initialization.GeetolSDK;
import com.gtdev5.geetolsdk.mylibrary.util.CPResourceUtils;
import com.gtdev5.geetolsdk.mylibrary.util.MapUtils;
import com.gtdev5.geetolsdk.mylibrary.util.SpUtils;
import com.gtdev5.geetolsdk.mylibrary.util.Utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cheng
 * PackageName ModelTest
 * 2018/1/4 9:28
 *          Http请求类
 */

public class HttpUtils {

    private static HttpUtils mHttpUtils;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    public static final int GET_HTTP_TYPE = 1;//get请求
    public static final int POST_HTTP_TYPE = 2;//post请求
    public static final int UPLOAD_HTTP_TYPE = 3;//上传请求
    public static final int DOWNLOAD_HTTP_TYPE = 4;//下载请求

    private Request request = null;

    private MessageDigest alga;

    private Map<String,String> resultMap;

    private String string;

    private boolean isHave;

    private Gson gson;
    
    private String commonUrl;

    private HttpUtils(){
        try {
            mOkHttpClient = new OkHttpClient();
            mOkHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS)
                    .writeTimeout(10,TimeUnit.SECONDS);
            mHandler = new Handler(Looper.getMainLooper());
            gson = new Gson();
            alga = MessageDigest.getInstance("SHA-1");
            //初始化域名
            commonUrl = SpUtils.getInstance().getString(Contants.COMMON_URL,API.COMMON_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    public static HttpUtils getInstance(){
        if (mHttpUtils == null){
            synchronized (HttpUtils.class){
                if (mHttpUtils == null){
                    mHttpUtils = new HttpUtils();
                }
            }
        }
        return mHttpUtils;
    }

    /**
     *          提供对外调用的请求接口
     * @param callBack      回调接口
     * @param url           路径
     * @param type          请求类型
     * @param paramKey      请求参数
     * @param paramValue    请求值
     */
    public static void httpsNetWorkRequest(final DataCallBack callBack, final String url, final int type, final String[] paramKey, final Object[] paramValue){
            getInstance().inner_httpsNetWorkRequest(callBack,url,type,paramKey,paramValue);
    }

    /**
     *          内部处理请求的方法
     * @param callBack      回调接口
     * @param url           路径
     * @param type          请求类型
     * @param paramKey      请求参数
     * @param paramValue    请求值
     */
    private void inner_httpsNetWorkRequest(final DataCallBack callBack,final String url,final int type,final String[] paramKey,final Object[] paramValue){
        RequestBody requestBody = null;
        FormBody.Builder builder = new FormBody.Builder();

        Map<String,String> map = new TreeMap<String,String>();

        map.put("appid", CPResourceUtils.getString("appid"));
//        map.put("key",CPResourceUtils.getString("appkey"));
        map.put("sign",null);
        map.put("device",CPResourceUtils.getDevice());

        if (paramKey != null){
            for (int i = 0; i < paramKey.length; i++) {
                map.put(paramKey[i],String.valueOf(paramValue[i]));
            }
            resultMap = sortMapByKey(map);
        }

        String str="";
        int num = 0;

        boolean isFirst = true;
        switch (type){
            case GET_HTTP_TYPE:
                request = new Request.Builder().url(commonUrl+url).build();
                break;
            case POST_HTTP_TYPE:
                /**
                 * 循环遍历获取key值，拼接sign字符串
                 */
                for (Map.Entry<String, String> entry :
                        resultMap.entrySet()) {
                    if (entry.getValue() == null){
                        continue;
                    }
                    num++;
                    if (isFirst){
                        str += entry.getKey() + "=" + Base64.encodeToString(entry.getValue().getBytes(),Base64.DEFAULT).trim();
                        isFirst = !isFirst;
                    }else {
                        str = str.trim();
                        str += "&" + entry.getKey() + "=" + Base64.encodeToString(entry.getValue().getBytes(),Base64.DEFAULT).trim();
                        if (num == resultMap.size() - 1){
                            str += "&" + "key" + "=" + CPResourceUtils.getString("appkey");
                        }
                    }
//                    if (isFirst){
//                        str = str.trim();
//                        if (entry.getKey().equals("key")){
//                            str += entry.getKey()+"="+entry.getValue();
//                        }else {
//                            str += entry.getKey() + "=" + android.util.Base64.encodeToString(entry.getValue().getBytes(), android.util.Base64.DEFAULT).trim();
//                        }
//                        isFirst  = !isFirst;
//                    }else {
//                        str = str.trim();
//                        if (entry.getKey().equals("key")){
//                            str += "&"+entry.getKey()+ "=" + entry.getValue();
//                        }else {
//                            str += "&" + entry.getKey() + "=" + android.util.Base64.encodeToString(entry.getValue().getBytes(), android.util.Base64.DEFAULT).trim();
//                        }
//                    }
                }

                    str = str.replace("\n","");//去除换行
                    str = str.replace("\\s","");//去除空格
//                Log.e("testaaaa",str);
                    isFirst = !isFirst;
                    alga.update(str.getBytes());

                /**
                 * 循环遍历value值，添加到表单
                 */
                for (Map.Entry<String, String> entry :
                        resultMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value == null) {
                        value = null;
                    }
                    if (key.equals("sign")) {
                        value = Utils.byte2hex(alga.digest());
                    }else if (key.equals("key")){
                        continue;
                    }
                    builder.add(key,value);
                }

                    requestBody = builder.build();
                request = new Request.Builder().url(commonUrl+ url).post(requestBody).build();
                break;
            case UPLOAD_HTTP_TYPE:
                MultipartBody.Builder multipartBody = new MultipartBody.Builder("-----").setType(MultipartBody.FORM);
                if (paramKey != null && paramValue != null){
                    for (int i = 0; i < paramKey.length; i++) {
                        multipartBody.addFormDataPart(paramKey[i],String.valueOf(paramValue[i]));
                    }
                    requestBody = multipartBody.build();
                }
                request = new Request.Builder().url(commonUrl+url).post(requestBody).build();
                break;
                default:
                    break;
        }

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request,e,callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    deliverDataFailure(request,e,callBack);
                }
                deliverDataSuccess(result,callBack);
            }
        });
    }

    /**
     *          分发失败的时候回调
     * @param request
     * @param e
     * @param callBack
     */
    private void deliverDataFailure(final Request request, final IOException e,final DataCallBack callBack){
        mHandler.post(()->{
           if (callBack != null){
               callBack.requestFailure(request,e);
           }
        });
    }

    /**
     *          分发成功的时候回调
     * @param result
     * @param callBack
     */
    private void deliverDataSuccess(final String result,final DataCallBack callBack){
        mHandler.post(()->{
            if (callBack != null){
                try {
                    callBack.requestSuceess(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *          map根据key值比较大小
     * @param map
     * @return
     */
    private static Map<String,String> sortMapByKey(Map<String,String> map){
        if (map == null || map.isEmpty()){
            return null;
        }

        Map<String,String> sortMap = new TreeMap<String,String>((str1,str2)-> str1.compareTo(str2));
        sortMap.putAll(map);
        return sortMap;
    }


    /**
     *      内部处理Map集合
     *      得到from表单 (post请求)
     * @return
     */
    private RequestBody getRequestBody(Map<String,String> map){
        RequestBody requestBody = null;
        FormBody.Builder builder = new FormBody.Builder();
        resultMap = sortMapByKey(map);

        Log.e("请求参数：","map:"+resultMap.toString());

        String str="";
        int num = 0;

        boolean isFirst = true;

        /**
         * 循环遍历获取key值，拼接sign字符串
         */
        for (Map.Entry<String, String> entry :
                resultMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            num++;
            if (isFirst) {
                str += entry.getKey() + "=" + Base64.encodeToString(entry.getValue().getBytes(), Base64.DEFAULT).trim();
                isFirst = !isFirst;
            } else {
                str = str.trim();
                str += "&" + entry.getKey() + "=" + Base64.encodeToString(entry.getValue().getBytes(), Base64.DEFAULT).trim();
                if (num == resultMap.size() - 1) {
                    str += "&" + "key" + "=" + CPResourceUtils.getString("appkey");
                }
            }
        }


        str = str.replace("\n","");//去除换行
        str = str.replace("\\s","");//去除空格

        Log.e("请求参数：","string:"+str);
//        Log.e("testaaaa",str);
        isFirst = !isFirst;
        alga.update(str.getBytes());


        /**
         * 循环遍历value值，添加到表单
         */
        for (Map.Entry<String, String> entry :
                resultMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                value = null;
            }
            if (key.equals("sign")) {
                value = Utils.byte2hex(alga.digest());
            }else if (key.equals("key")){
                continue;
            }
            builder.add(key,value);
        }

        requestBody = builder.build();
        return requestBody;
    }

    /**---------------------------------------------------------------------------分割线-------------------------------------------------------------------------*/


//    /**
//     *      内部请求方法
//     * @param request
//     * @return
//     */
//    private String getResult(Request request){
//        Call newCall = mOkHttpClient.newCall(request);
//        newCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                string = response.body().string();
//                isHave = true;
//            }
//        });
//
//        while (isHave){
//            if (string != null && !string.equals("")) {
//                isHave = false;
//                return string;
//            }
//        }
//        return string;
//    }

//    /**
//     *          返回泛型的json数据对象
//     * @param url               请求路径
//     * @param requestBody       表单
//     * @param tClass            泛型
//     * @param <T>               返回类型
//     * @return
//     */
//    private <T> T backResponse(String url, RequestBody requestBody, Class<T> tClass){
//        T t = null;
//        request = new Request.Builder().url(url).post(requestBody).build();
//        if (getResult(request) != null){
//            t = GsonUtils.getFromClass(getResult(request),tClass);
//        }
//        return t;
//    }

    //    /**
//     *      提供给外部调用的注册接口
//     * @param tClass        泛型类
//     * @param <T>           返回类型
//     * @return
//     */
//    public static <T> T getRegister(Class<T> tClass){
//        return getInstance().inner_getRegister(tClass);
//    }
//
//    /**
//     *      内部处理注册接口方法
//     * @param tClass        泛型类
//     * @param <T>           返回类型
//     * @return
//     */
//    private <T> T inner_getRegister(Class<T> tClass){
//        T t = null;
//        RequestBody requestBody = getRequestBody(MapUtils.getRegistMap());
//        t = backResponse(commonUrl + API.REGIST_DEVICE, requestBody, tClass);
//        return t;
//    }
//
//    /**
//     *      提供给外部调用的更新数据接口
//     * @param tClass        泛型类
//     * @param <T>           返回类型
//     * @return
//     */
//    public static <T> T getUpdate(Class<T> tClass){
//        return getInstance().inner_getUpdate(tClass);
//    }
//
//    /**
//     *      内部处理更新数据接口方法
//     * @param tClass        泛型类
//     * @param <T>           返回类型
//     * @return
//     */
//    private <T> T inner_getUpdate(Class<T> tClass){
//        T t = null;
//        RequestBody requestBody = getRequestBody(MapUtils.getCurrencyMap());
//        t = backResponse(commonUrl + API.UPDATE,requestBody,tClass);
//        return t;
//    }
//
//    /**
//     *      提供给外部调用了版本更新接口
//     * @param tClass        泛型类
//     * @param <T>           返回类型
//     * @return
//     */
//    public static <T> T getNews(Class<T> tClass){
//        return getInstance().inner_getNews(tClass);
//    }
//
//    /**
//     *      内部处理更新数据接口方法
//     * @param tClass        泛型类
//     * @param <T>           返回类型
//     * @return
//     */
//    private <T> T inner_getNews(Class<T> tClass){
//        T t = null;
//        RequestBody requestBody = getRequestBody(MapUtils.getNewMap());
//        t = backResponse(commonUrl+API.GETNEW,requestBody,tClass);
//        return t;
//    }
//
//    /**
//     *      提供给外部调用的意见反馈接口
//     * @param tClass        泛型类
//     * @param content       文本内容
//     * @param phone         联系方式
//     * @param <T>           返回类型
//     * @return
//     */
//    public static <T> T getFeedBack(Class<T> tClass,String content,String phone){
//        return getInstance().inner_getFeedBack(tClass,content,phone);
//    }
//
//    /**
//     *      内部处理意见反馈接口
//     * @param tClass        泛型类
//     * @param content       文本内容
//     * @param phone         联系方式
//     * @param <T>           返回类型
//     * @return
//     */
//    private <T> T inner_getFeedBack(Class<T>tClass,String content,String phone){
//        T t = null;
//        RequestBody requestBody = getRequestBody(MapUtils.getFeedBack(content, phone));
//        t = backResponse(commonUrl+API.FEEDBACK,requestBody,tClass);
//        return t;
//    }
    /**---------------------------------------------------------------------------分割线-------------------------------------------------------------------------*/

    //
    /**
     *      获取app的下载链接
     * @param callback      回调函数
     */
    public void postGetAppUrl(long apid,BaseCallback callback){
        post(commonUrl+API.GET_APPURL,MapUtils.getAppUrlMap(apid),callback);

    }

    /**
     *      提供给外部调用的添加服务单接口
     * @param callback      回调函数
     */
    public void postAddService(String title,String descibe,String type,String img,BaseCallback callback){
        post(commonUrl+API.ADD_SERVICE,MapUtils.getAddServiceMap(title,descibe,type,img),callback);
    }


    /**
     * 获取服务单接口
     * @param page
     * @param limit
     * @param callback
     */
    public void postGetServices(int page,int limit,BaseCallback callback){
        post(commonUrl+API.GET_SERVICE,MapUtils.getGetServiceMap(page,limit),callback);
    }

    /**
     * 获取服务单详情的接口
     * @param service_id
     * @param callback
     */
    public void postGetServicesDetails(int service_id,BaseCallback callback){
        post(commonUrl+API.GET_SERVICE_DETAILS,MapUtils.getServiceDetialsMap(service_id),callback);
    }



    /**
     *
     * 添加服务单回复接口
     * @param service_id   服务单id
     * @param repley       回复内容
     * @param img           回复图片   base64处理的图片 多个用，分割
     * @param callback
     */
    public void postAddRepley(int service_id,String repley,String img,BaseCallback callback){
        post(commonUrl+API.ADD_REPLEY,MapUtils.getAddRepleyMap(service_id,repley,img),callback);
    }

    /**
     * 结束服务
     * @param id        服务单id
     * @param callback
     */
    public void postEndService(int id,BaseCallback callback){
        post(commonUrl+API.END_SERVICE,MapUtils.getServiceDetialsMap(id),callback);
    }

    /**
     *      提供给外部调用的注册接口
     * @param callback      回调函数
     */
    public void postRegister(BaseCallback callback){
        post(commonUrl+API.REGIST_DEVICE, MapUtils.getRegistMap(),callback);
    }

    /**
     *      提供给外部调用的更新数据接口
     * @param callback      回调函数
     */
    public void postUpdate(BaseCallback callback){
        post(commonUrl+API.UPDATE,MapUtils.getCurrencyMap(),callback);
    }




    /**
     *      提供给外部调用的版本更新接口
     * @param callback      回调函数
     */
    public void postNews(BaseCallback callback){
        post(commonUrl+API.GETNEW,MapUtils.getNewMap(),callback);
    }

    /**
     *      提供给外部调用的意见反馈接口
     * @param content       意见内容
     * @param phone         联系方式
     * @param callback      回调函数
     */
    public void postFeedBack(String content,String phone,BaseCallback callback){
        post(commonUrl+API.FEEDBACK,MapUtils.getFeedBack(content,phone),callback);
    }

    /**
     *      提供给外部调用的支付订单接口
     * @param type          订单类型    1:支付    2:打赏
     * @param pid           商品ID
     * @param amount        打赏订单必填,支付可不填
     * @param pway          支付类型    1:微信    2:支付宝
     * @param callback      回调函数
     */
    public void postOrder(int type,int pid,float amount,int pway,BaseCallback callback){
        post(commonUrl+API.ORDER_ONE,MapUtils.getOrder(type,pid,amount,pway),callback);
    }


    /**
     *      新接口
     *      提供给外部调用的支付订单接口
     * @param type          订单类型    1:支付    2:打赏
     * @param pid           商品ID
     * @param amount        打赏订单必填,支付可不填
     * @param pway          支付类型    1:微信    2:支付宝
     * @param callback      回调函数
     */
    public void PostOdOrder(int type,int pid,float amount,int pway,BaseCallback callback){
        post(commonUrl+API.ORDER_OD,MapUtils.getOrder(type,pid,amount,pway),callback);
    }

    /**
     *      内部提供的post请求方法
     * @param url           请求路径
     * @param params        请求参数(表单)
     * @param callback      回调函数
     */
    public void post(String url, Map<String,String> params, final BaseCallback callback){
        //请求之前调用(例如加载动画)
        callback.onRequestBefore();
          mOkHttpClient.newCall(getRequest(url,params)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //返回失败
                callbackFailure(call.request(),callback,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    //返回成功回调
                    String result = response.body().string();
                    Log.e("请求数据：",result);
                    if (callback.mType == String.class){
                        //如果我们需要返回String类型
                        callbackSuccess(response,result,callback);
                    }else {
                        //如果返回是其他类型,则用Gson去解析

                        try {
                            Object o = gson.fromJson(result, callback.mType);
                            callbackSuccess(response,o,callback);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            callbackError(response,callback,e);
                        }
                    }
                }else {
                    callbackError(response,callback,null);
                }

            }
        });

    }

    /**
     *      得到Request
     * @param url           请求路径
     * @param params        from表单
     * @return
     */
    private Request getRequest(String url,Map<String,String> params){
         //可以从这么划分get和post请求，暂时只支持post
         Log.e("请求参数：","url:"+url);
         return new Request.Builder().url(url).post(getRequestBody(params)).build();
    }

    /**
     *      在主线程中执行成功回调
     * @param response      请求响应
     * @param o             类型
     * @param callback      回调函数
     */
    private void callbackSuccess(final Response response, final Object o, final BaseCallback<Object> callback){
        mHandler.post(()->callback.onSuccess(response,o));
    }

    /**
     *      在主线程中执行错误回调
     * @param response      请求响应
     * @param callback      回调函数
     * @param e             响应错误异常
     */
    private void callbackError(final Response response,final BaseCallback callback,Exception e){
        mHandler.post(()->callback.onError(response,response.code(),e));
    }

    /**
     *      在主线程中执行失败回调
     * @param request       请求链接
     * @param callback      回调韩素和
     * @param e             响应错误异常
     */
    private void callbackFailure(final Request request,final BaseCallback callback,final Exception e){
        mHandler.post(()->callback.onFailure(request,e));
    }









}

package com.gtdev5.geetolsdk.mylibrary.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeoy
 * PackageName ModelTest
 * 2018年3月29日15:44:39
 *          json解析工具
 */

public class GsonUtils {

    /**
     * 解析bean
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> String getStringFromBean (T bean){
        Gson gson = new Gson();
        return gson.toJson(bean);
    }

    /**
     *          解析对象
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getFromClass(String json,Class<T> tClass){
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(json,tClass);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     *          解析集合
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T>List<T> getFromList(String json,Class<T> tClass){
        List<T> list = new ArrayList<>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(json,new TypeToken<List<T>>(){}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return list;
    }

}
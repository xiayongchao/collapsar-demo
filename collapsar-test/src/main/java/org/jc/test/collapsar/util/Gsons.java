package org.jc.test.collapsar.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * json格式化工具
 *
 * @author xiayc
 * @date 2018/8/20
 */
public class Gsons {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static class SingletonHolder {
        private SingletonHolder() {
        }

        private final static Gson INSTANCE = new GsonBuilder().serializeNulls().disableHtmlEscaping().setDateFormat(DATE_FORMAT).create();
    }

    public static Gson getPrototype() {
        return new GsonBuilder().create();
    }

    private static Gson getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static Gson getSingleton() {
        return getInstance();
    }

    /**
     * 将对象转化为字符串
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String getJson(T t) {
        return getInstance().toJson(t);
    }

    /**
     * 将字符串转化为对象
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getObject(String jsonString, Class<T> clazz) {
        return getInstance().fromJson(jsonString, clazz);
    }

    /**
     * 将字符串转化为对象
     *
     * @param jsonString
     * @param typeToken
     * @param <T>
     * @return
     */
    public static <T> List<T> getObject(String jsonString, TypeToken typeToken) {
        return getInstance().fromJson(jsonString, typeToken.getType());
    }

    /**
     * 将字符串转化为数组
     *
     * @param jsonString
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T[] getArray(String jsonString, Class<T> tClass) {
        return getInstance().fromJson(jsonString, TypeToken.getArray(tClass).getType());
    }

    /**
     * 将字符串转化为列表
     *
     * @param jsonString
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getList(String jsonString, Class<T> tClass) {
        T[] ts = getInstance().fromJson(jsonString, TypeToken.getArray(tClass).getType());
        return ts == null ? null : Arrays.asList(ts);
    }

    /**
     * 将字符串转化为Map
     *
     * @param jsonStr
     * @param kClass
     * @param vClass
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> getMap(String jsonStr, Class<K> kClass, Class<V> vClass) {
        return getInstance().fromJson(jsonStr, new TypeToken<Map<K, V>>() {
        }.getType());
    }
}

package org.jc.framework.collapsar.extend;

import java.lang.reflect.Type;

/**
 * @author yc_xia
 */
public interface CacheRepository {
    /**
     * 获取缓存
     *
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    <T> T get(String key, Type type);

    /**
     * 设置缓存
     *
     * @param key
     * @param object
     * @param expire 过期时间/ms
     * @param <T>
     * @return
     */
    <T> void set(String key, T object, long expire);

    /**
     * 删除缓存
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> void del(String key);
}

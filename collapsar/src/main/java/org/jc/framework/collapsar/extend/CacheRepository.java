package org.jc.framework.collapsar.extend;

/**
 * @author yc_xia
 */
public interface CacheRepository {
    /**
     * 获取缓存
     *
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    <T> T get(String key, Class<T> tClass);

    /**
     * 设置缓存
     *
     * @param key
     * @param object
     * @param expire 过期时间/ms
     * @param <T>
     * @return
     */
    <T> boolean set(String key, T object, long expire);

    /**
     * 删除缓存
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> boolean del(String key);
}

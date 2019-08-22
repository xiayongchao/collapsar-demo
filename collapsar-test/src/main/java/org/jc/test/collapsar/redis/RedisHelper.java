package org.jc.test.collapsar.redis;

import com.alibaba.fastjson.JSON;
import org.jc.framework.collapsar.core.CacheRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @author xiayc
 * @date 2018/10/25
 */
@Component
public class RedisHelper implements CacheRepository {
    private Map<String, String> cacheMap = new HashMap<>();

    @Override
    public <T> T get(String key, Class<T> tClass) {
        String json = cacheMap.get(key);
        System.out.println(String.format("get object {key:%s,value:%s}", key, json));
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, tClass);
    }

    @Override
    public <T> boolean set(String key, T object, long expire) {
        String value = JSON.toJSONString(object);
        cacheMap.put(key, value);
        System.out.println(String.format("set object {key:%s,value:%s,expire:%s}", key, value, expire));
        return true;
    }

    @Override
    public <T> boolean del(String key) {
        cacheMap.remove(key);
        System.out.println(String.format("remove object {key:%s}", key));
        return true;
    }
}

package org.jc.test.collapsar.redis;

import com.alibaba.fastjson.JSON;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.test.collapsar.util.Gsons;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * @author xiayc
 * @date 2018/10/25
 */
@Component
public class RedisHelper implements CacheRepository {
    private static Map<String, String> cacheMap = new HashMap<>();

    static {
        cacheMap.put("comment:User:id_1", "{\"id\":1,\"userName\":\"xxx\",\"password\":\"123\"}");
    }

    @Override
    public <T> T get(String key, Type type) {
        String json = cacheMap.get(key);
        System.err.println(String.format("get {key:%s,value:%s}", key, json));
        if (json == null) {
            return null;
        }
        return Gsons.getObject(json, (Class<T>) type);
    }

    @Override
    public <T> void set(String key, T object, long expire) {
        String value = JSON.toJSONString(object);
        cacheMap.put(key, value);
        System.err.println(String.format("set {key:%s,value:%s,expire:%s}", key, value, expire));
    }

    @Override
    public <T> void del(String key) {
        cacheMap.remove(key);
        System.err.println(String.format("remove {key:%s}", key));
    }
}

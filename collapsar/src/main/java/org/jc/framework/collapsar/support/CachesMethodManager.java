package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.handler.MethodParseHandler;
import org.jc.framework.collapsar.util.Methods;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jc
 * @date 2019/8/22 1:17
 */
public class CachesMethodManager {
    private final Map<String, CachesMethodSupporter> cachesMethodSupporterMap = new ConcurrentHashMap<>();

    public void register(final Method method, final CachesBeanDefinition cachesBeanDefinition) {
        final String methodFullName = String.format("%s#%s", method.getDeclaringClass().getName(), method.getName());
        if (cachesMethodSupporterMap.containsKey(methodFullName)) {
            throw new CollapsarException("请不要重复注册@Caches Bean方法['%s']", methodFullName);
        }
        final CachesMethod cachesMethod = MethodParseHandler.parseHandleMethod(method, new MethodDefinition(cachesBeanDefinition));

        final CachesMethodSupporter cachesMethodSupporter = new CachesMethodSupporter(cachesMethod);
        cachesMethodSupporterMap.put(methodFullName, cachesMethodSupporter);
    }

    public CachesMethodSupporter get(Method method) {
        final String methodFullName = Methods.getMethodFullName(method);
        CachesMethodSupporter cachesMethodSupporter = cachesMethodSupporterMap.get(methodFullName);
        if (cachesMethodSupporter == null) {
            throw new CollapsarException("无法获取未注册的@Caches Bean方法['%s']", methodFullName);
        }
        return cachesMethodSupporter;
    }
}

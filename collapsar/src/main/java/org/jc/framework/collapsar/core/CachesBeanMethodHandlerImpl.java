package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.definition.CachesBeanDefinition;

import java.lang.reflect.Method;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class CachesBeanMethodHandlerImpl implements CachesBeanMethodHandler {
    private final CacheKeyManager cacheKeyManager = new CacheKeyManager();

    /**
     * 注册方法
     *
     * @param method
     * @param cachesBeanDefinition
     */
    @Override
    public void registerMethod(Method method, CachesBeanDefinition cachesBeanDefinition) {
        cacheKeyManager.registerMethod(new MethodDefinition(method), cacheName, cacheTarget);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed,
                         Object[] args) throws Throwable {
        return null;
    }
}

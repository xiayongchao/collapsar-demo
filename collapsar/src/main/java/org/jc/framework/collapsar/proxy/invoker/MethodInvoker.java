package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;

/**
 * @author xiayc
 * @date 2019/8/30
 */
public interface MethodInvoker {
    Object invoke(CacheRepository cacheRepository, Object[] args) throws InvocationTargetException, IllegalAccessException;
}

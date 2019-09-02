package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xiayc
 * @date 2019/8/30
 */
public interface MethodInvoker {
    Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException;

    default Object invokeProceedMethod(Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (proceed != null) {
            return proceed.invoke(self, args);
        }
        return null;
    }
}

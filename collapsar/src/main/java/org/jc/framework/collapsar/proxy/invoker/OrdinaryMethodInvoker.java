package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class OrdinaryMethodInvoker extends PenetrationsMethodInvoker {
    @Override
    public Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return invokePenetrationMethod(self, proceed, args);
    }
}

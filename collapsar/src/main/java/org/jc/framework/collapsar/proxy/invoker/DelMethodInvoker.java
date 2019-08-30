package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class DelMethodInvoker extends AbstractMethodInvoker {
    @Override
    public Object invoke(CacheRepository cacheRepository, Object[] args) throws InvocationTargetException, IllegalAccessException {
        cacheRepository.del(generateKey(args));
        return invokePenetrationMethod(args);
    }
}

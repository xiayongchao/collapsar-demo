package org.jc.framework.collapsar.support.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.support.CachesDelMethod;
import org.jc.framework.collapsar.support.CachesMethodWrapper;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class DelMethodInvoker extends MethodInvoker {

    protected DelMethodInvoker(CacheRepository cacheRepository) {
        super(cacheRepository);
    }

    @Override
    protected Object invoke(Object[] args, CachesMethodWrapper cachesMethodWrapper) throws InvocationTargetException, IllegalAccessException {
        CachesDelMethod cachesDelMethod = cachesMethodWrapper.getCachesMethod();
        cacheRepository.del(cachesDelMethod.generateKey(args));
        return cachesDelMethod.invokePenetrationMethod(args);
        /
    }
}

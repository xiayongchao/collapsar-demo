package org.jc.framework.collapsar.core.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class BatchDelMethodInvoker extends AbstractBatchMethodInvoker {
    @Override
    public Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        int size = calcListSize(args);
        Object[] filterArgs;
        for (int i = 0; i < size; i++) {
            filterArgs = filterArgs(i, args);
            cacheRepository.del(generateKey(filterArgs));
        }
        return invokeProceedMethod(self, proceed, args);
    }
}

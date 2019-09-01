package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class BatchSetMethodInvoker extends AbstractBatchMethodInvoker {
    /**
     * 缓存过期时间
     */
    private long expire = 0;
    private int valueParameterIndex = -1;

    @Override
    public Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        int size = calcListSize(args);
        Object[] filterArgs;
        for (int i = 0; i < size; i++) {
            filterArgs = filterArgs(i, args);
            cacheRepository.set(generateKey(filterArgs), selectValueParameter(i, args),
                    expire);
        }
        return invokePenetrationMethod(self, proceed, args);
    }

    private Object selectValueParameter(int i, Object[] args) {
        List arg = (List) args[valueParameterIndex];
        return arg == null ? null : arg.get(i);
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setValueParameterIndex(int valueParameterIndex) {
        this.valueParameterIndex = valueParameterIndex;
    }
}

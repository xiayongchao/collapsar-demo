package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class SetMethodInvoker extends AbstractMethodInvoker {
    /**
     * 缓存过期时间
     */
    private long expire = 0;
    private int valueParameterIndex = -1;

    @Override
    public Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        cacheRepository.set(generateKey(args), selectValueParameter(args),
                expire);
        return invokePenetrationMethod(self, proceed, args);
    }

    private Object selectValueParameter(Object[] args) {
        return args[valueParameterIndex];
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setValueParameterIndex(int valueParameterIndex) {
        this.valueParameterIndex = valueParameterIndex;
    }
}

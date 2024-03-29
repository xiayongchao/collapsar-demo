package org.jc.framework.collapsar.core.invoker;

import org.jc.framework.collapsar.extend.CacheRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class GetMethodInvoker extends AbstractMethodInvoker {
    private Type returnType;

    @Override
    public Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Object object = cacheRepository.get(generateKey(args), returnType);
        if (object == null) {
            object = invokeProceedMethod(self, proceed, args);
        }
        return object;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }
}

package org.jc.framework.collapsar.proxy;


import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.support.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class CachesBeanMethodHandlerImpl implements CachesBeanMethodHandler {
    private final CachesMethodManager cachesMethodManager = new CachesMethodManager();
    private final CacheRepository cacheRepository;

    public CachesBeanMethodHandlerImpl(CacheRepository cacheRepository) {
        if (cacheRepository == null) {
            throw new CollapsarException("请提供[%s]的Spring Bean实例", CacheRepository.class.getName());
        }
        this.cacheRepository = cacheRepository;
    }

    /**
     * 注册方法
     *
     * @param method
     * @param cachesBeanDefinition
     */
    @Override
    public void registerMethod(Method method, Object penetrationsBean, CachesBeanDefinition cachesBeanDefinition) {
        cachesMethodManager.register(method, penetrationsBean, cachesBeanDefinition);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed,
                         Object[] args) throws Throwable {
        final CachesMethodSupporter cachesMethodSupporter = cachesMethodManager.get(thisMethod);

        switch (cachesMethodSupporter.getOperate()) {
            case SET:
                return invokeSetMethod(args, cachesMethodSupporter);
            case GET:
                return invokeGetMethod(args, cachesMethodSupporter);
            case DEL:
                return invokeDelMethod(args, cachesMethodSupporter);
            default:
                throw new CollapsarException("未知的@Caches Bean方法[%s]操作类型:[%s]",
                        cachesMethodSupporter.getMethodFullName(), cachesMethodSupporter.getOperate().toString());
        }
    }

    private Object invokeGetMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesGetMethod cachesGetMethod = cachesMethodSupporter.getCachesMethod();
        Object object = cacheRepository.get(cachesGetMethod.generateKey(args), cachesGetMethod.getReturnType());
        if (object == null) {
            object = cachesMethodSupporter.invokePenetrationMethod(args);
        }
        return object;
    }

    private Object invokeSetMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesSetMethod cachesSetMethod = cachesMethodSupporter.getCachesMethod();
        cacheRepository.set(cachesSetMethod.generateKey(args), cachesSetMethod.selectValueParameter(args),
                cachesSetMethod.getExpire());
        return cachesMethodSupporter.invokePenetrationMethod(args);
    }

    private Object invokeDelMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesDelMethod cachesDelMethod = cachesMethodSupporter.getCachesMethod();
        cacheRepository.del(cachesDelMethod.generateKey(args));
        return cachesMethodSupporter.invokePenetrationMethod(args);
    }
}

package org.jc.framework.collapsar.proxy;


import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.support.CachesMethodManager;
import org.jc.framework.collapsar.support.CachesMethodSupporter;
import org.jc.framework.collapsar.support.CachesSetMethod;

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
    public void registerMethod(Method method, CachesBeanDefinition cachesBeanDefinition) {
        cachesMethodManager.register(method, cachesBeanDefinition);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed,
                         Object[] args) throws Throwable {
        final CachesMethodSupporter cachesMethodSupporter = cachesMethodManager.get(thisMethod);
        switch (cachesMethodSupporter.getOperate()) {
            case SET:
                CachesSetMethod cachesSetMethod = cachesMethodSupporter.getCachesMethod();
                return cacheRepository.set(cachesSetMethod.buildCacheKey(args), cachesSetMethod.selectValueParameter(args),
                        cachesSetMethod.getExpire());
            case GET:
                return cacheRepository.get(cachesMethodSupporter.buildCacheKey(args), cachesMethodSupporter.getReturnType());
            case DEL:
                return cacheRepository.del(cachesMethodSupporter.buildCacheKey(args));
            default:
                throw new CollapsarException("未知的@Caches Bean方法[%s]操作类型:[%s]", cachesMethodSupporter.getMethodFullName(), cachesMethodSupporter.getOperate().toString());
        }
    }
}

package org.jc.framework.collapsar.proxy;


import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.support.*;
import org.springframework.util.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
            case BATCH_SET:
                return invokeBatchSetMethod(args, cachesMethodSupporter);
            case BATCH_GET:
                return invokeBatchGetMethod(args, cachesMethodSupporter);
            case BATCH_DEL:
                return invokeBatchDelMethod(args, cachesMethodSupporter);
            default:
                throw new CollapsarException("未知的@Caches Bean方法[%s]操作类型:[%s]",
                        cachesMethodSupporter.getMethodFullName(), cachesMethodSupporter.getOperate().toString());
        }
    }

    private Object invokeBatchSetMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesBatchSetMethod cachesBatchSetMethod = cachesMethodSupporter.getCachesMethod();
        int size = cachesBatchSetMethod.calcListSize(args);
        Object[] filterArgs;
        for (int i = 0; i < size; i++) {
            filterArgs = cachesBatchSetMethod.filterArgs(i, args);
            cacheRepository.set(cachesBatchSetMethod.generateKey(filterArgs), cachesBatchSetMethod.selectValueParameter(i, args),
                    cachesBatchSetMethod.getExpire());
        }
        return cachesMethodSupporter.invokePenetrationMethod(args);
    }

    private Object invokeBatchGetMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesBatchGetMethod cachesBatchGetMethod = cachesMethodSupporter.getCachesMethod();
        int size = cachesBatchGetMethod.calcListSize(args);
        Object[] filterArgs;
        Type returnType = cachesBatchGetMethod.getReturnType();
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType;
        List<Object> list;
        try {
            list = cachesBatchGetMethod.getImplType().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException e) {
            throw new CollapsarException(e, "执行[%s]方法失败,implType[%s]请提供无参构造函数", cachesMethodSupporter.getMethodFullName(),
                    cachesBatchGetMethod.getImplType().getName());
        }
        Object object;
        List<Integer> noHitIndexList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            filterArgs = cachesBatchGetMethod.filterArgs(i, args);
            object = cacheRepository.get(cachesBatchGetMethod.generateKey(filterArgs), parameterizedType.getActualTypeArguments()[0]);
            if (object == null) {
                noHitIndexList.add(i);
            } else {
                list.add(object);
            }
        }
        if (!CollectionUtils.isEmpty(noHitIndexList)) {
            filterArgs = cachesBatchGetMethod.filterArgs(noHitIndexList, args);
            Object noHitList = cachesMethodSupporter.invokePenetrationMethod(filterArgs);
            if (noHitList != null) {
                list.addAll((List<?>) noHitList);
            }
        }
        return list;
    }

    private Object invokeBatchDelMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesBatchDelMethod cachesBatchDelMethod = cachesMethodSupporter.getCachesMethod();
        int size = cachesBatchDelMethod.calcListSize(args);
        Object[] filterArgs;
        for (int i = 0; i < size; i++) {
            filterArgs = cachesBatchDelMethod.filterArgs(i, args);
            cacheRepository.del(cachesBatchDelMethod.generateKey(filterArgs));
        }
        return cachesMethodSupporter.invokePenetrationMethod(args);
    }

    private Object invokeSetMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesSetMethod cachesSetMethod = cachesMethodSupporter.getCachesMethod();
        cacheRepository.set(cachesSetMethod.generateKey(args), cachesSetMethod.selectValueParameter(args),
                cachesSetMethod.getExpire());
        return cachesMethodSupporter.invokePenetrationMethod(args);
    }

    private Object invokeGetMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesGetMethod cachesGetMethod = cachesMethodSupporter.getCachesMethod();
        Object object = cacheRepository.get(cachesGetMethod.generateKey(args), cachesGetMethod.getReturnType());
        if (object == null) {
            object = cachesMethodSupporter.invokePenetrationMethod(args);
        }
        return object;
    }

    private Object invokeDelMethod(Object[] args, CachesMethodSupporter cachesMethodSupporter) throws InvocationTargetException, IllegalAccessException {
        CachesDelMethod cachesDelMethod = cachesMethodSupporter.getCachesMethod();
        cacheRepository.del(cachesDelMethod.generateKey(args));
        return cachesMethodSupporter.invokePenetrationMethod(args);
    }
}

package org.jc.framework.collapsar.proxy;


import org.jc.framework.collapsar.definition.MethodDefinition;
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
            throw new CollapsarException("使用Collapsar必须提供[%s]的Spring Bean实例", CacheRepository.class.getName());
        }
        this.cacheRepository = cacheRepository;
    }

    /**
     * 注册方法
     *
     * @param method
     * @param methodDefinition
     */
    @Override
    public void registerMethod(Method method, Object penetrationsBean, MethodDefinition methodDefinition) {
        cachesMethodManager.register(method, penetrationsBean, methodDefinition);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed,
                         Object[] args) throws Throwable {
        final CachesMethodWrapper cachesMethodWrapper = cachesMethodManager.get(thisMethod);

        switch (cachesMethodWrapper.getOperate()) {
            case SET:
                return invokeSetMethod(args, cachesMethodWrapper.getCachesMethod());
            case GET:
                return invokeGetMethod(args, cachesMethodWrapper.getCachesMethod());
            case DEL:
                return invokeDelMethod(args, cachesMethodWrapper.getCachesMethod());
            case BATCH_SET:
                return invokeBatchSetMethod(args, cachesMethodWrapper.getCachesMethod());
            case BATCH_GET:
                return invokeBatchGetMethod(args, cachesMethodWrapper.getCachesMethod());
            case BATCH_DEL:
                return invokeBatchDelMethod(args, cachesMethodWrapper.getCachesMethod());
            case NONE:
                return invokeNoneMethod(args, cachesMethodWrapper.getCachesMethod());
            default:
                throw new CollapsarException("未知的@Caches Bean方法[%s]操作类型:[%s]",
                        cachesMethodWrapper.getMethodFullName(), cachesMethodWrapper.getOperate().toString());
        }
    }

    private Object invokeNoneMethod(Object[] args, CachesNoneMethod cachesNoneMethod) throws InvocationTargetException, IllegalAccessException {
        return cachesNoneMethod.invokePenetrationMethod(args);
    }

    private Object invokeBatchSetMethod(Object[] args, CachesBatchSetMethod cachesBatchSetMethod) throws InvocationTargetException, IllegalAccessException {
        int size = cachesBatchSetMethod.calcListSize(args);
        Object[] filterArgs;
        for (int i = 0; i < size; i++) {
            filterArgs = cachesBatchSetMethod.filterArgs(i, args);
            cacheRepository.set(cachesBatchSetMethod.generateKey(filterArgs), cachesBatchSetMethod.selectValueParameter(i, args),
                    cachesBatchSetMethod.getExpire());
        }
        return cachesBatchSetMethod.invokePenetrationMethod(args);
    }

    private Object invokeBatchGetMethod(Object[] args, CachesBatchGetMethod cachesBatchGetMethod) throws InvocationTargetException, IllegalAccessException {
        int size = cachesBatchGetMethod.calcListSize(args);
        Object[] filterArgs;
        Type returnType = cachesBatchGetMethod.getReturnType();
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType;
        List<Object> list;
        try {
            list = cachesBatchGetMethod.getImplType().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException e) {
            throw new CollapsarException(e, "执行[%s]方法失败,implType[%s]请提供无参构造函数", cachesBatchGetMethod.getMethodFullName(),
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
            Object noHitList = cachesBatchGetMethod.invokePenetrationMethod(filterArgs);
            if (noHitList != null) {
                list.addAll((List<?>) noHitList);
            }
        }
        return list;
    }

    private Object invokeBatchDelMethod(Object[] args, CachesBatchDelMethod cachesBatchDelMethod) throws InvocationTargetException, IllegalAccessException {
        int size = cachesBatchDelMethod.calcListSize(args);
        Object[] filterArgs;
        for (int i = 0; i < size; i++) {
            filterArgs = cachesBatchDelMethod.filterArgs(i, args);
            cacheRepository.del(cachesBatchDelMethod.generateKey(filterArgs));
        }
        return cachesBatchDelMethod.invokePenetrationMethod(args);
    }

    private Object invokeSetMethod(Object[] args, CachesSetMethod cachesSetMethod) throws InvocationTargetException, IllegalAccessException {
        cacheRepository.set(cachesSetMethod.generateKey(args), cachesSetMethod.selectValueParameter(args),
                cachesSetMethod.getExpire());
        return cachesSetMethod.invokePenetrationMethod(args);
    }

    private Object invokeGetMethod(Object[] args, CachesGetMethod cachesGetMethod) throws InvocationTargetException, IllegalAccessException {
        Object object = cacheRepository.get(cachesGetMethod.generateKey(args), cachesGetMethod.getReturnType());
        if (object == null) {
            object = cachesGetMethod.invokePenetrationMethod(args);
        }
        return object;
    }

    private Object invokeDelMethod(Object[] args, CachesDelMethod cachesDelMethod) throws InvocationTargetException, IllegalAccessException {
        cacheRepository.del(cachesDelMethod.generateKey(args));
        return cachesDelMethod.invokePenetrationMethod(args);
    }
}

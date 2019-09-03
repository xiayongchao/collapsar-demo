package org.jc.framework.collapsar.core.invoker;

import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.core.builder.ParameterKeyBuilder;
import org.jc.framework.collapsar.core.parser.Optional;
import org.springframework.util.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public class BatchGetMethodInvoker extends AbstractBatchMethodInvoker {
    private Type returnType;
    private Class<? extends List> implType;

    @Override
    public Object invoke(CacheRepository cacheRepository, Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        int size = calcListSize(args);
        Object[] filterArgs;
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType;
        List<Object> list;
        try {
            list = implType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException e) {
            throw new CollapsarException(e, "执行[%s]方法失败,implType[%s]请提供无参构造函数", methodFullName,
                    implType.getName());
        }
        Object object;
        List<Integer> noHitIndexList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            filterArgs = filterArgs(i, args);
            object = cacheRepository.get(generateKey(filterArgs), parameterizedType.getActualTypeArguments()[0]);
            if (object == null) {
                noHitIndexList.add(i);
            } else {
                list.add(object);
            }
        }
        if (!CollectionUtils.isEmpty(noHitIndexList)) {
            filterArgs = filterArgs(noHitIndexList, args);
            Object penetrationResult = invokeProceedMethod(self, proceed, filterArgs);
            if (penetrationResult != null) {
                if (penetrationResult instanceof Optional) {
                    Optional optional = (Optional) penetrationResult;
                    if (optional.get() != null) {
                        list.addAll(optional.get());
                    }
                    noHitIndexList = optional.getNoHitIndexList();
                } else {
                    list.addAll((List<?>) penetrationResult);
                }
            }
        }
        if (((ParameterizedTypeImpl) returnType).getRawType().equals(Optional.class)) {
            return Optional.of(list, noHitIndexList);
        }
        return list;
    }

    private Object[] filterArgs(List<Integer> indexList, Object[] args) {
        Object[] filterArgs = new Object[args.length];
        System.arraycopy(args, 0, filterArgs, 0, args.length);
        Object arg;
        Iterator iterator;
        int i;
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            arg = filterArgs[parameterKeyBuilder.getIndex()];
            if (parameterKeyBuilder.isBatch()) {
                iterator = arg == null ? null : ((List) arg).iterator();
                i = 0;
                while (arg != null && iterator.hasNext()) {
                    iterator.next();
                    if (indexList != null && !indexList.contains(i)) {
                        iterator.remove();
                    }
                    i++;
                }
            }
        }
        return filterArgs;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public void setImplType(Class<? extends List> implType) {
        this.implType = implType;
    }
}

package org.jc.framework.collapsar.proxy;


import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.handler.MethodParseHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class CollapsarBeanMethodHandlerImpl implements CollapsarBeanMethodHandler {
    private final Map<String, MethodInvoker> methodInvokerMap = new ConcurrentHashMap<>();
    private final CacheRepository cacheRepository;

    public CollapsarBeanMethodHandlerImpl(CacheRepository cacheRepository) {
        if (cacheRepository == null) {
            throw new CollapsarException("使用Collapsar必须提供[%s]的Spring Bean实例", CacheRepository.class.getName());
        }
        this.cacheRepository = cacheRepository;
    }

    @Override
    public void registerMethod(Method method, MethodDefinition methodDefinition) {
        final String methodFullName = method.toString();
        if (methodInvokerMap.containsKey(methodFullName)) {
            throw new CollapsarException("请不要重复注册@Caches/@MultiCaches Bean方法['%s']", methodFullName);
        }
        final MethodInvoker methodInvoker = MethodParseHandler.parseHandleMethod(method, methodDefinition);
        methodInvokerMap.put(methodFullName, methodInvoker);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed,
                         Object[] args) throws Throwable {
        MethodInvoker methodInvoker = methodInvokerMap.get(thisMethod.toString());
        if (methodInvoker == null) {
            return proceed.invoke(self, args);
        }
        return methodInvoker.invoke(cacheRepository, self, proceed, args);
    }
}

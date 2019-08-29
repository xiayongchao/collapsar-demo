package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.handler.MethodParseHandler;
import org.jc.framework.collapsar.util.Methods;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jc
 * @date 2019/8/22 1:17
 */
public class CachesMethodManager {
    private final Map<String, CachesMethodWrapper> cachesMethodSupporterMap = new ConcurrentHashMap<>();

    public void register(final Method method, final Object penetrationsBean, final MethodDefinition methodDefinition) {
        final String methodFullName = method.toString();
        if (cachesMethodSupporterMap.containsKey(methodFullName)) {
            throw new CollapsarException("请不要重复注册@Caches/@MultiCaches Bean方法['%s']", methodFullName);
        }
        final CachesMethod cachesMethod = MethodParseHandler.parseHandleMethod(method, methodDefinition);
        Method penetrationsMethod;
        try {
            penetrationsMethod = penetrationsBean != null ? penetrationsBean.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()) : null;
        } catch (NoSuchMethodException e) {
            throw new CollapsarException(e, "获取@Penetrations Bean[%s]方法[%s]失败", penetrationsBean.getClass().getName(), method.getName());
        }

        final CachesMethodWrapper cachesMethodWrapper = new CachesMethodWrapper(cachesMethod, penetrationsBean, penetrationsMethod);
        cachesMethodSupporterMap.put(methodFullName, cachesMethodWrapper);

    }

    public CachesMethodWrapper get(Method method) {
        final String methodFullName = Methods.getMethodFullName(method);
        CachesMethodWrapper cachesMethodWrapper = cachesMethodSupporterMap.get(methodFullName);
        if (cachesMethodWrapper == null) {
            throw new CollapsarException("无法获取未注册的@Caches/@MultiCaches Bean方法[%s]", methodFullName);
        }
        return cachesMethodWrapper;
    }
}

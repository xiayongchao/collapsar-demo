package org.jc.framework.collapsar.proxy;

import javassist.util.proxy.MethodHandler;
import org.jc.framework.collapsar.definition.CachesBeanDefinition;

import java.lang.reflect.Method;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public interface CachesBeanMethodHandler extends MethodHandler {
    /**
     * 注册方法
     *
     * @param method
     * @param penetrationsBean
     * @param cachesBeanDefinition
     */
    void registerMethod(Method method, Object penetrationsBean, CachesBeanDefinition cachesBeanDefinition);
}

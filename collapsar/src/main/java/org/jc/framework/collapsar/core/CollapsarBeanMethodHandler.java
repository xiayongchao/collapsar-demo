package org.jc.framework.collapsar.core;

import javassist.util.proxy.MethodHandler;
import org.jc.framework.collapsar.definition.MethodDefinition;

import java.lang.reflect.Method;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public interface CollapsarBeanMethodHandler extends MethodHandler {
    /**
     * 注册方法
     *
     * @param method
     * @param methodDefinition
     */
    void registerMethod(Method method, MethodDefinition methodDefinition);
}

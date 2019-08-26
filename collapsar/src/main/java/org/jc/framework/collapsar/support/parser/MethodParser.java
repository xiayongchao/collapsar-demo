package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.handler.ParameterParseHandler;
import org.jc.framework.collapsar.util.Methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author jc
 * @date 2019/8/26 21:59
 */
public abstract class MethodParser {
    protected final static String METHOD_NAME_SEPARATOR = "And";
    protected final Method method;
    protected final Class<?> targetType;
    protected final String methodFullName;
    protected final MethodDefinition methodDefinition;

    MethodParser(Method method, Class<?> targetType) {
        this.method = method;
        this.targetType = targetType;
        this.methodFullName = Methods.getMethodFullName(method);
        this.methodDefinition = new MethodDefinition();
    }

    private static MethodParser getInstance(Operate operate, Method method, Class<?> targetType) {
        switch (operate) {
            case SET:
                return new SetMethodParser(method, targetType);
            default:
                throw new CollapsarException("未知的@Caches方法[%s$%s]操作类型[%s]", method.getDeclaringClass().getName(), method.getName(), operate);
        }
    }

    public static MethodDefinition parseMethod(Operate operate, Method method, Class<?> targetType) {
        return getInstance(operate, method, targetType).parseClassName()
                .parseMethodName().parseMethodOperate()
                .parseMethodParameter().parseMethodReturnType().get();
    }

    MethodParser parseClassName() {
        methodDefinition.setClassName(method.getDeclaringClass().getName());
        return this;
    }

    MethodParser parseMethodName() {
        methodDefinition.setMethodName(method.getName());
        return this;
    }

    MethodParser parseMethodOperate() {
        return this;
    }

    MethodParser parseMethodParameter() {
        return this;
    }

    ParameterDefinition[] getParameterDefinitions() {
        Type[] parameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        ParameterDefinition[] parameterDefinitions = new ParameterDefinition[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterDefinitions[i] = ParameterParseHandler.praseHandleParameter(methodFullName, parameterTypes[i], parameterAnnotations[i]);
        }
        return parameterDefinitions;
    }

    MethodParser parseMethodReturnType() {
        return this;
    }

    private MethodDefinition get() {
        return methodDefinition;
    }
}
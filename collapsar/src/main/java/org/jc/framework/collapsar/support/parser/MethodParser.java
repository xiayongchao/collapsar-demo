package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;
import org.jc.framework.collapsar.support.builder.ReflectParameterKeyBuilder;
import org.jc.framework.collapsar.support.builder.SimpleParameterKeyBuilder;
import org.jc.framework.collapsar.support.handler.ParameterParseHandler;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.jc.framework.collapsar.util.Methods;
import org.jc.framework.collapsar.util.Strings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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
        ParameterDefinition parameterDefinition;
        Set<String> existNameSet = new HashSet<>();
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterDefinition = ParameterParseHandler.parseHandleParameter(methodFullName, parameterTypes[i], parameterAnnotations[i]);
            if (!ParamType.NONE.equals(parameterDefinition.getParamType())) {
                for (String name : parameterDefinition.getNames()) {
                    if (existNameSet.contains(name)) {
                        throw new CollapsarException("方法[%s]不允许提供重复的Key命名", methodFullName);
                    }
                    existNameSet.add(name);
                }
            }
            parameterDefinitions[i] = parameterDefinition;
        }
        return parameterDefinitions;
    }

    private ParameterKeyBuilder getParameterKeyBuilder(String parameterName, ParameterDefinition[] parameterDefinitions) {
        ParameterDefinition parameterDefinition;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //优先从@Key注解匹配
            if (!ParamType.KEY.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if (parameterName.equals(parameterDefinition.getNames()[0])) {
                return new SimpleParameterKeyBuilder(i, parameterName);
            }
        }
        String[] names;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //然后再从@Keys
            if (!ParamType.KEYS.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if (ArrayUtils.isEmpty(names = parameterDefinition.getNames())) {
                continue;
            }
            for (String name : names) {
                if (!parameterName.equals(name)) {
                    continue;
                }
                try {
                    Field declaredField = ((Class) parameterDefinition.getType()).getDeclaredField(parameterName);
                    declaredField.setAccessible(true);
                    return new ReflectParameterKeyBuilder(i, parameterName, declaredField);
                } catch (NoSuchFieldException e) {
                    throw new CollapsarException(e, "方法[%s]参数Key[%s]获取失败", methodFullName, parameterName);
                }
                break;
            }
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //然后再从@Value注解匹配
            if (!ParamType.VALUE.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if (ArrayUtils.isEmpty(names = parameterDefinition.getNames())) {
                continue;
            }
            for (String name : names) {
                if (!parameterName.equals(name)) {
                    continue;
                }
                if (names.length == 1) {
                    if (parameterDefinition.getType().equals(String.class)
                            || parameterDefinition.getType().equals(Byte.class)
                            || parameterDefinition.getType().equals(Short.class)
                            || parameterDefinition.getType().equals(Integer.class)
                            || parameterDefinition.getType().equals(Long.class)
                            || parameterDefinition.getType().equals(Float.class)
                            || parameterDefinition.getType().equals(Double.class)
                            || parameterDefinition.getType().equals(Boolean.class)
                            || parameterDefinition.getType().equals(Character.class)) {
                        return new SimpleParameterKeyBuilder(i, parameterName);
                    }
                }
                try {
                    Field declaredField = ((Class) parameterDefinition.getType()).getDeclaredField(parameterName);
                    declaredField.setAccessible(true);
                    return new ReflectParameterKeyBuilder(i, parameterName, declaredField);
                } catch (NoSuchFieldException e) {
                    throw new CollapsarException(e, "方法[%s]参数Key[%s]获取失败", methodFullName, parameterName);
                }
                break;
            }
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.NONE.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {

            }
        }

//        else {
//            try {
//                Field declaredField = ((Class) parameterDefinition.getType()).getDeclaredField(parameterName);
//                declaredField.setAccessible(true);
//                return new ReflectParameterKeyBuilder(i, parameterName, declaredField);
//            } catch (NoSuchFieldException e) {
//            }
//        }

    }

    ParameterKeyBuilder[] getParameterKeyBuilders(String[] parameterNames, ParameterDefinition[] parameterDefinitions) {
        ParameterKeyBuilder[] parameterKeyBuilders = new ParameterKeyBuilder[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++) {
            parameterKeyBuilders[i] = getParameterKeyBuilder(Strings.standingInitialLowercase(parameterNames[i])
                    , parameterDefinitions);
        }
        return parameterKeyBuilders;
    }

    MethodParser parseMethodReturnType() {
        return this;
    }

    private MethodDefinition get() {
        return methodDefinition;
    }
}

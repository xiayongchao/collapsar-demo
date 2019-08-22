package org.jc.framework.collapsar.core;

import org.jc.framework.collapsar.annotation.*;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParamDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/21 23:59
 */
public class CachesMethodParser {
    private Method method;
    private Class<?> targetType;
    private MethodDefinition methodDefinition;

    private CachesMethodParser(Method method, Class<?> targetType) {
        this.method = method;
        this.targetType = targetType;
        this.methodDefinition = new MethodDefinition();
    }

    private static CachesMethodParser getInstance(Method method, Class<?> targetType) {
        return new CachesMethodParser(method, targetType);
    }

    private CachesMethodParser parseClassName() {
        methodDefinition.setClassName(method.getDeclaringClass().getName());
        return this;
    }

    private CachesMethodParser parseMethodName() {
        methodDefinition.setMethodName(method.getName());
        return this;
    }

    private CachesMethodParser parseMethodOperate() {
        if (method.isAnnotationPresent(SetOperate.class)) {
            if (!method.getName().startsWith(Operate.SET.getPrefix())) {
                throw new CollapsarException("[@SetOperate]注解方法[%s#%s]请使用['%s']前缀",
                        methodDefinition.getClassName(), methodDefinition.getMethodName(), Operate.SET.getPrefix());
            }
            methodDefinition.setOperate(Operate.SET);
            SetOperate setOperate = method.getDeclaredAnnotation(SetOperate.class);
            methodDefinition.setExpire(ExpireCalculator.calc(setOperate.expire(), setOperate.unit()));
        } else if (method.isAnnotationPresent(GetOperate.class)) {
            if (!method.getName().startsWith(Operate.GET.getPrefix())) {
                throw new CollapsarException("[@GetOperate]注解方法[%s#%s]请使用['%s']前缀",
                        methodDefinition.getClassName(), methodDefinition.getMethodName(), Operate.GET.getPrefix());
            }
            methodDefinition.setOperate(Operate.GET);
        } else if (method.isAnnotationPresent(DelOperate.class)) {
            if (!method.getName().startsWith(Operate.DEL.getPrefix())) {
                throw new CollapsarException("[@DelOperate]注解方法[%s#%s]请使用['%s']前缀",
                        methodDefinition.getClassName(), methodDefinition.getMethodName(), Operate.DEL.getPrefix());
            }
            methodDefinition.setOperate(Operate.DEL);
        } else {
            throw new CollapsarException("请在方法[%s#%s]上使用注解[@SetOperate/@GetOperate/@DelOperate]",
                    methodDefinition.getClassName(), methodDefinition.getMethodName());
        }
        return this;
    }

    private CachesMethodParser parseMethodParameter() {
        Type[] parameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        ParamDefinition[] paramDefinitions = new ParamDefinition[parameterTypes.length];
        Annotation[] annotations;
        String name;
        Boolean isValue;
        String[] parameterKeys = method.getName().substring(methodDefinition.getOperate().getPrefix().length()).split("And");
        Map<String, Integer> parameterKey2OrderMap = new HashMap<>(parameterKeys.length);
        for (int i = 0; i < parameterKeys.length; i++) {
            parameterKey2OrderMap.put(Strings.standingInitialLowercase(parameterKeys[i]), i);
        }
        Integer keyOrder;
        int valueParamCount = 0;
        for (int i = 0; i < parameterTypes.length; i++) {
            if ((annotations = parameterAnnotations[i]) == null
                    || annotations.length == 0) {
                throw new CollapsarException("请在方法[%s#%s]的形参上使用注解[@Key/@Value]",
                        methodDefinition.getClassName(), methodDefinition.getMethodName());
            }
            name = null;
            isValue = null;
            for (Annotation annotation : annotations) {
                if (annotation == null) {
                    continue;
                }
                if (annotation instanceof Key) {
                    if (isValue != null) {
                        throw new CollapsarException("[@SetOperate]注解的方法[%s#%s]的同一形参上只能使用注解[@Key]和[@Value]中的一个",
                                methodDefinition.getClassName(), methodDefinition.getMethodName());
                    }
                    isValue = false;
                    name = ((Key) annotation).value();
                    if (StringUtils.isEmpty(name)) {
                        throw new CollapsarException("方法[%s#%s]的@Key形参value值不能为空",
                                methodDefinition.getClassName(), methodDefinition.getMethodName());
                    }
                }
                if (annotation instanceof Value) {
                    if (isValue != null) {
                        throw new CollapsarException("[@SetOperate]注解的方法[%s#%s]的同一形参上只能使用注解[@Key]和[@Value]中的一个",
                                methodDefinition.getClassName(), methodDefinition.getMethodName());
                    }
                    if (!Operate.SET.equals(methodDefinition.getOperate())) {
                        throw new CollapsarException("非[@SetOperate]注解的方法[%s#%s]形参中禁止使用注解[@Value]",
                                methodDefinition.getClassName(), methodDefinition.getMethodName());
                    }
                    isValue = true;
                    valueParamCount++;
                }
            }
            if (name == null && isValue == null) {
                throw new CollapsarException("请在方法[%s#%s]的形参上使用注解[@Key/@Value]",
                        methodDefinition.getClassName(), methodDefinition.getMethodName());
            }
            if (isValue) {
                if (valueParamCount > 1) {
                    throw new CollapsarException("[@SetOperate]注解的方法[%s#%s]的形参中有且只能有一个使用注解[@Value]",
                            methodDefinition.getClassName(), methodDefinition.getMethodName());
                }
                if (!parameterTypes[i].equals(targetType)) {
                    throw new CollapsarException("方法[%s#%s]的@Value形参类型请使用[%s]",
                            methodDefinition.getClassName(), methodDefinition.getMethodName(), targetType.getName());
                }
                keyOrder = -1;
                methodDefinition.setValueParameterIndex(i);
            } else {
                if ((keyOrder = parameterKey2OrderMap.get(name)) == null) {
                    throw new CollapsarException("方法[%s#%s]未定义的@Key参数[%s]",
                            methodDefinition.getClassName(), methodDefinition.getMethodName(), name);
                }
                parameterKey2OrderMap.remove(name);
            }
            paramDefinitions[i] = new ParamDefinition(parameterTypes[i], name, isValue, i, keyOrder);
        }
        if (parameterKey2OrderMap.size() > 0) {
            StringJoiner stringJoiner = new StringJoiner(",");
            parameterKey2OrderMap.keySet().forEach(stringJoiner::add);
            throw new CollapsarException("方法[%s#%s]未提供的命名参数[%s]",
                    methodDefinition.getClassName(), methodDefinition.getMethodName(), stringJoiner.toString());
        }
        Arrays.sort(paramDefinitions);
        methodDefinition.setParamDefinitions(paramDefinitions);
        return this;
    }

    private CachesMethodParser parseMethodReturnType() {
        if (Operate.GET.equals(methodDefinition.getOperate())) {
            if (!method.getReturnType().equals(targetType)) {
                throw new CollapsarException("方法[%s#%s]的返回值类型请使用[%s]",
                        methodDefinition.getClassName(), methodDefinition.getMethodName(), targetType.getName());
            }
        } else {
            if (!method.getReturnType().equals(Void.TYPE)) {
                throw new CollapsarException("方法[%s#%s]的返回值类型请使用[%s]",
                        methodDefinition.getClassName(), methodDefinition.getMethodName(), Void.TYPE.getName());
            }
        }
        methodDefinition.setReturnType(method.getReturnType());
        return this;
    }

    private MethodDefinition get() {
        return methodDefinition;
    }

    public static MethodDefinition parseMethod(Method method, Class<?> targetType) {
        return getInstance(method, targetType).parseClassName()
                .parseMethodName().parseMethodOperate()
                .parseMethodParameter().parseMethodReturnType().get();
    }
}

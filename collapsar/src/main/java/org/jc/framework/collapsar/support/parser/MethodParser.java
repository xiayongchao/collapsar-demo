package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;
import org.jc.framework.collapsar.support.builder.ReflectParameterKeyBuilder;
import org.jc.framework.collapsar.support.builder.SimpleParameterKeyBuilder;
import org.jc.framework.collapsar.support.handler.ParameterParseHandler;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * 方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:59
 */
public abstract class MethodParser {
    protected final static String METHOD_NAME_SEPARATOR = "And";
    protected final Operate operate;
    protected final Method method;
    protected final MethodDefinition methodDefinition;
    protected final Object penetrationsBean;
    protected String[] parameterNames;
    protected ParameterDefinition[] parameterDefinitions;

    public MethodParser(Operate operate, Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        if (!Operate.NONE.equals(operate) && method.isDefault()) {
            throw new CollapsarException("interface default方法[%s]不支持使用[%s]", method.toString(), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        this.operate = operate;
        this.method = method;
        this.methodDefinition = methodDefinition;
        this.penetrationsBean = penetrationsBean;
    }

    public MethodInvoker getMethodInvoker() {
        return this.parseMethodFullName()
                .parseMethodProjectName().parseMethodModuleName().parseMethodConnector()
                .parseMethodOperate().parseMethodParameter().parseMethodReturnType().parseMethodPenetrations()
                .get();
    }

    protected MethodParser parseMethodFullName() {
        get().setMethodFullName(method.toString());
        return this;
    }

    protected MethodParser parseMethodProjectName() {
        get().setProjectName(methodDefinition.getProjectName());
        return this;
    }

    protected MethodParser parseMethodModuleName() {
        get().setModuleName(methodDefinition.getModuleName());
        return this;
    }

    protected MethodParser parseMethodConnector() {
        get().setConnector(methodDefinition.getConnector());
        return this;
    }

    protected MethodParser parseMethodOperate() {
        boolean multi;
        String name;
        if (!operate.validatePrefix(name = method.getName(), multi = methodDefinition.isMulti())) {
            throw new CollapsarException("[%s]注解方法[%s]请使用['%s']前缀",
                    operate.getName(), get().getMethodFullName(), multi ? operate.getMultiPrefix() : operate.getPrefix());
        }
        if (multi) {
            get().setModuleName(operate.getModuleName(name));
        }
        return this;
    }

    protected MethodParser parseMethodParameterNames() {
        String nominateKey = operate.removePrefix(method.getName(), get().getModuleName(), methodDefinition.isMulti());
        if (StringUtils.isEmpty(nominateKey)) {
            throw new CollapsarException("非法的[%s]方法[%s]命名,请提供Key的名称", operate.getName(), get().getMethodFullName());
        }
        parameterNames = nominateKey.split(METHOD_NAME_SEPARATOR);
        return this;
    }

    protected MethodParser parseMethodParameterDefinitions() {
        Type[] parameterTypes = method.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        parameterDefinitions = new ParameterDefinition[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterDefinitions[i] = ParameterParseHandler.parseHandleParameter(get().getMethodFullName(), parameterTypes[i], parameterAnnotations[i]);
        }
        if (ArrayUtils.isEmpty(parameterDefinitions)) {
            throw new CollapsarException("[%s]方法[%s]入参不能为空", operate.getName(), get().getMethodFullName());
        }
        return this;
    }

    protected int getValueParameterIndex() {
        int valueParameterIndex = -1;
        Type valueParameterType = null;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (!ParamType.VALUE.equals(parameterDefinitions[i].getParamType())) {
                continue;
            }
            if (valueParameterType != null) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中有且只能有一个参数使用注解[@Value]",
                        operate.getName(), get().getMethodFullName());
            }
            valueParameterType = parameterDefinitions[i].getType();
            valueParameterIndex = i;
        }
        if (valueParameterType == null) {
            throw new CollapsarException("[%s]注解的方法[%s]必须提供一个@Value注解的参数",
                    operate.getName(), get().getMethodFullName());
        }
        return valueParameterIndex;
    }

    protected void batchOperateMethodValueParameter() {
        ParameterDefinition parameterDefinition;
        Type parameterType;
        ParameterizedTypeImpl parameterizedType = null;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.VALUE.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中不能有参数使用注解[@Value]",
                        operate.getName(), get().getMethodFullName());
            }
            if ((parameterType = parameterDefinition.getType()) instanceof ParameterizedTypeImpl) {
                parameterizedType = (ParameterizedTypeImpl) parameterType;
                try {
                    (parameterizedType.getRawType()).asSubclass(List.class);
                } catch (ClassCastException e) {
                    throw new CollapsarException(e, "[%s]注解的方法[%s]不支持的参数类型[%s]",
                            operate.getName(), get().getMethodFullName(), parameterType.getTypeName());
                }
            }
        }
        if (parameterizedType == null) {
            throw new CollapsarException("[%s]注解的方法[%s]必须提供的参数类型[%s]",
                    operate.getName(), get().getMethodFullName(), List.class.getName());
        }
    }

    protected MethodParser parseMethodParameterValue() {
        return this;
    }

    protected MethodParser parseMethodParameterKeyBuilders() {
        Set<String> provideNames = new HashSet<>();
        for (ParameterDefinition parameterDefinition : parameterDefinitions) {
            if (ParamType.NONE.equals(parameterDefinition.getParamType())) {
                continue;
            }
            for (String name : parameterDefinition.getNames()) {
                if (StringUtils.isEmpty(name)) {
                    continue;
                }
                if (provideNames.contains(name)) {
                    throw new CollapsarException("方法[%s]不允许提供重复的Key命名[%s]", get().getMethodFullName(), name);
                }
                provideNames.add(name);
            }
        }

        ParameterKeyBuilder[] parameterKeyBuilders = new ParameterKeyBuilder[parameterNames.length];
        String parameterName;
        for (int i = 0; i < parameterNames.length; i++) {
            parameterKeyBuilders[i] = getParameterKeyBuilder(parameterName = Strings.standingInitialLowercase(parameterNames[i])
                    , parameterDefinitions);
            provideNames.remove(parameterName);
        }
        if (!CollectionUtils.isEmpty(provideNames)) {
            StringJoiner stringJoiner = new StringJoiner(",");
            for (String provideName : provideNames) {
                stringJoiner.add(provideName);
            }
            throw new CollapsarException("方法[%s]没有用到的Key命名[%s]", get().getMethodFullName(), stringJoiner.toString());
        }
        get().setParameterKeyBuilders(parameterKeyBuilders);
        return this;
    }

    protected MethodParser parseMethodParameter() {
        return parseMethodParameterNames().parseMethodParameterDefinitions().
                parseMethodParameterValue().parseMethodParameterKeyBuilders();
    }

    protected long calcExpire(long expire, TimeUnit unit) {
        return unit.convert(expire, TimeUnit.MILLISECONDS);
    }

    private ParameterKeyBuilder getReflectParameterKeyBuilder(String parameterName, int i, String[] names, boolean isBatch, Class<?> parameterType, boolean isForce) {
        if (isForce) {
            if (ArrayUtils.isNotEmpty(names)) {
                return null;
            }
            try {
                Field declaredField = parameterType.getDeclaredField(parameterName);
                declaredField.setAccessible(true);
                return new ReflectParameterKeyBuilder(i, parameterName, isBatch, declaredField);
            } catch (NoSuchFieldException ignored) {
            }
        } else {
            if (ArrayUtils.isEmpty(names)) {
                return null;
            }
            for (String name : names) {
                if (!parameterName.equals(name)) {
                    continue;
                }
                try {
                    Field declaredField = parameterType.getDeclaredField(parameterName);
                    declaredField.setAccessible(true);
                    return new ReflectParameterKeyBuilder(i, parameterName, isBatch, declaredField);
                } catch (NoSuchFieldException e) {
                    throw new CollapsarException(e, "方法[%s]参数Key[%s]获取失败", get().getMethodFullName(), parameterName);
                }
            }
        }
        return null;
    }

    private Class<?> getParameterRealType(boolean isBatch, Type parameterType) {
        if (isBatch) {
            if (parameterType instanceof ParameterizedTypeImpl) {
                ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) parameterType;
                return (Class<?>) parameterizedType.getActualTypeArguments()[0];
            }
        }

        return (Class<?>) parameterType;
    }

    private boolean isBatch() {
        return Operate.BATCH_SET.equals(operate) || Operate.BATCH_GET.equals(operate) || Operate.BATCH_DEL.equals(operate);
    }

    private ParameterKeyBuilder getParameterKeyBuilder(String parameterName, ParameterDefinition[] parameterDefinitions) {
        ParameterDefinition parameterDefinition;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //优先从@Key注解匹配
            if (!ParamType.KEY.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if (parameterName.equals(parameterDefinition.getNames()[0])) {
                return new SimpleParameterKeyBuilder(i, parameterName, isBatch());
            }
        }
        ParameterKeyBuilder parameterKeyBuilder;
        boolean isBatch;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //然后再从@Keys
            if (!ParamType.KEYS.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if ((parameterKeyBuilder = getReflectParameterKeyBuilder(parameterName, i,
                    parameterDefinition.getNames(), isBatch = isBatch(),
                    getParameterRealType(isBatch, parameterDefinition.getType()), false)) != null) {
                return parameterKeyBuilder;
            }
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //然后再从@Value注解匹配
            if (!ParamType.VALUE.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if ((parameterKeyBuilder = getReflectParameterKeyBuilder(parameterName, i,
                    parameterDefinition.getNames(), isBatch = isBatch(),
                    getParameterRealType(isBatch, parameterDefinition.getType()), false)) != null) {
                return parameterKeyBuilder;
            }
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            //然后再从@Keys
            if (!ParamType.KEYS.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if ((parameterKeyBuilder = getReflectParameterKeyBuilder(parameterName, i,
                    parameterDefinition.getNames(), isBatch = isBatch(),
                    getParameterRealType(isBatch, parameterDefinition.getType()), true)) != null) {
                return parameterKeyBuilder;
            }
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (!ParamType.VALUE.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                continue;
            }
            if ((parameterKeyBuilder = getReflectParameterKeyBuilder(parameterName, i,
                    parameterDefinition.getNames(), isBatch = isBatch(),
                    getParameterRealType(isBatch, parameterDefinition.getType()), true)) != null) {
                return parameterKeyBuilder;
            }
        }
        throw new CollapsarException("请提供方法[%s]需要的参数Key[%s]", get().getMethodFullName(), parameterName);
    }

    protected MethodParser parseMethodReturnType() {
        return this;
    }

    protected MethodParser parseMethodPenetrations() {
        Method penetrationsMethod;
        try {
            penetrationsMethod = penetrationsBean != null ? penetrationsBean.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()) : null;
        } catch (NoSuchMethodException e) {
            throw new CollapsarException(e, "获取@Penetrations Bean[%s]方法[%s]失败", penetrationsBean.getClass().getName(), method.getName());
        }
        get().setPenetrationsBean(penetrationsBean);
        get().setPenetrationsMethod(penetrationsMethod);
        return this;
    }

    protected abstract AbstractMethodInvoker get();
}

package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.CachesMethod;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;
import org.jc.framework.collapsar.support.builder.ReflectParameterKeyBuilder;
import org.jc.framework.collapsar.support.builder.SimpleParameterKeyBuilder;
import org.jc.framework.collapsar.support.handler.ParameterParseHandler;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.jc.framework.collapsar.util.Methods;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/26 21:59
 */
public abstract class MethodParser {
    protected final static String METHOD_NAME_SEPARATOR = "And";
    protected final Method method;
    protected final MethodDefinition methodDefinition;
    protected final String methodFullName;
    protected final CachesMethod cachesMethod;
    protected final Operate operate;

    MethodParser(Operate operate, Method method, MethodDefinition methodDefinition) {
        this.method = method;
        this.methodDefinition = methodDefinition;
        this.methodFullName = Methods.getMethodFullName(method);
        this.cachesMethod = new CachesMethod(methodDefinition.getProjectName(),
                methodDefinition.getModuleName(), methodDefinition.getConnector(), this.methodFullName);
        this.operate = operate;
    }

    private static MethodParser getInstance(Operate operate, Method method, MethodDefinition methodDefinition) {
        switch (operate) {
            case SET:
                return new SetMethodParser(Operate.SET, method, methodDefinition);
            case GET:
                return new GetMethodParser(Operate.GET, method, methodDefinition);
            case DEL:
                return new DelMethodParser(Operate.DEL, method, methodDefinition);
            case BATCH_SET:
                return new BatchSetMethodParser(Operate.BATCH_SET, method, methodDefinition);
            case BATCH_GET:
                return new BatchGetMethodParser(Operate.BATCH_GET, method, methodDefinition);
            case BATCH_DEL:
                return new BatchDelMethodParser(Operate.BATCH_DEL, method, methodDefinition);
            default:
                throw new CollapsarException("未知的@Caches方法[%s]操作类型[%s]", Methods.getMethodFullName(method), operate);
        }
    }

    public static CachesMethod parseMethod(Operate operate, Method method, MethodDefinition methodDefinition) {
        return getInstance(operate, method, methodDefinition).parseMethodOperate()
                .parseMethodParameter().parseMethodReturnType().get();
    }

    MethodParser parseMethodOperate() {
        boolean multi;
        String name;
        if (!operate.validatePrefix(name = method.getName(), multi = methodDefinition.isMulti())) {
            throw new CollapsarException("[%s]注解方法[%s]请使用['%s']前缀",
                    operate.getName(), methodFullName, multi ? operate.getMultiPrefix() : operate.getPrefix());
        }
        if (multi) {
            cachesMethod.setModuleName(operate.getModuleName(name));
        }
        cachesMethod.setOperate(operate);
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
            parameterDefinitions[i] = ParameterParseHandler.parseHandleParameter(methodFullName, parameterTypes[i], parameterAnnotations[i]);
        }
        return parameterDefinitions;
    }

    ParameterKeyBuilder[] getParameterKeyBuilders(String[] parameterNames, ParameterDefinition[] parameterDefinitions) {
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
                    throw new CollapsarException("方法[%s]不允许提供重复的Key命名[%s]", methodFullName, name);
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
            throw new CollapsarException("方法[%s]没有用到的Key命名[%s]", methodFullName, stringJoiner.toString());
        }
        return parameterKeyBuilders;
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
            } catch (NoSuchFieldException e) {
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
                    throw new CollapsarException(e, "方法[%s]参数Key[%s]获取失败", methodFullName, parameterName);
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

    protected boolean isBatch() {
        if (Operate.BATCH_SET.equals(operate) || Operate.BATCH_GET.equals(operate) || Operate.BATCH_DEL.equals(operate)) {
            return true;
        }
        return false;
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
        throw new CollapsarException("请提供方法[%s]需要的参数Key[%s]", methodFullName, parameterName);
    }

    MethodParser parseMethodReturnType() {
        return this;
    }

    private CachesMethod get() {
        return cachesMethod;
    }
}

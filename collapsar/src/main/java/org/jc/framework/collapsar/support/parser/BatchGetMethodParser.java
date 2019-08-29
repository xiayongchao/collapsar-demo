package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.annotation.BatchGetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jc
 * @date 2019/8/26 21:58
 */
public class BatchGetMethodParser extends MethodParser {
    public BatchGetMethodParser(Operate operate, Method method, MethodDefinition methodDefinition) {
        super(operate, method, methodDefinition);
    }

    @Override
    MethodParser parseMethodOperate() {
        if (!method.getName().startsWith(operate.getPrefix())) {
            throw new CollapsarException("[%s]注解方法[%s]请使用['%s']前缀",
                    operate.getName(), methodFullName, operate.getPrefix());
        }
        cachesMethod.setOperate(operate);
        return this;
    }

    @Override
    MethodParser parseMethodParameter() {
        String nominateKey = method.getName().substring(operate.getPrefix().length());
        if (StringUtils.isEmpty(nominateKey)) {
            throw new CollapsarException("非法的[%s]方法[%s]命名,请提供Key的名称", operate.getName(), methodFullName);
        }
        String[] parameterNames = nominateKey.split(METHOD_NAME_SEPARATOR);
        ParameterDefinition[] parameterDefinitions = getParameterDefinitions();
        if (ArrayUtils.isEmpty(parameterDefinitions)) {
            throw new CollapsarException("[%s]方法[%s]入参不能为空", operate.getName(), methodFullName);
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.VALUE.equals(parameterDefinitions[i].getParamType())) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中不能有参数使用注解[@Value]",
                        operate.getName(), methodFullName);
            }
        }
        cachesMethod.setParameterKeyBuilders(getParameterKeyBuilders(parameterNames, parameterDefinitions));
        return this;
    }

    @Override
    MethodParser parseMethodReturnType() {
        Type returnType = method.getAnnotatedReturnType().getType();
        if (!(returnType instanceof ParameterizedTypeImpl)) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s<%s>]或其实现类",
                    methodFullName, List.class.getName(), methodDefinition.getTargetType().getName());
        }
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType;
        try {
            (parameterizedType.getRawType()).asSubclass(List.class);
        } catch (ClassCastException e) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s<%s>]或其实现类",
                    methodFullName, List.class.getName(), methodDefinition.getTargetType().getName());
        }
        if (!parameterizedType.getActualTypeArguments()[0].equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s<%s>]或其实现类",
                    methodFullName, List.class.getName(), methodDefinition.getTargetType().getName());
        }
        cachesMethod.setReturnType(returnType);
        BatchGetOperate batchGetOperate = method.getDeclaredAnnotation(BatchGetOperate.class);
        cachesMethod.setImplType(batchGetOperate.implType());
        return this;
    }
}

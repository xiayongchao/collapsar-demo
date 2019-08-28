package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.annotation.BatchSetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.ExpireCalculator;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author jc
 * @date 2019/8/26 21:58
 */
public class BatchSetMethodParser extends MethodParser {
    BatchSetMethodParser(Operate operate, Method method, MethodDefinition methodDefinition) {
        super(operate, method, methodDefinition);
    }

    @Override
    MethodParser parseMethodOperate() {
        if (!method.getName().startsWith(operate.getPrefix())) {
            throw new CollapsarException("[%s]注解方法[%s]请使用['%s']前缀",
                    operate.getName(), methodFullName, operate.getPrefix());
        }
        cachesMethod.setOperate(operate);
        BatchSetOperate batchSetOperate = method.getDeclaredAnnotation(BatchSetOperate.class);
        cachesMethod.setExpire(ExpireCalculator.calc(batchSetOperate.expire(), batchSetOperate.unit()));
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
        Type valueParameterType = null;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (!ParamType.VALUE.equals(parameterDefinitions[i].getParamType())) {
                continue;
            }
            if (valueParameterType != null) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中有且只能有一个参数使用注解[@Value]",
                        operate.getName(), methodFullName);
            }
            valueParameterType = parameterDefinitions[i].getType();
            cachesMethod.setValueParameterIndex(i);
        }
        if (valueParameterType == null) {
            throw new CollapsarException("[%s]注解的方法[%s]必须提供一个@Value注解的参数",
                    operate.getName(), methodFullName);
        }
        if (!valueParameterType.equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的@Value形参类型请使用[%s]",
                    methodFullName, methodDefinition.getTargetType().getName());
        }
        cachesMethod.setParameterKeyBuilders(getParameterKeyBuilders(parameterNames, parameterDefinitions));
        return this;
    }

    @Override
    MethodParser parseMethodReturnType() {
        if (!Void.TYPE.equals(method.getReturnType())) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s]",
                    methodFullName, Void.TYPE.getName());
        }
        cachesMethod.setReturnType(method.getReturnType());
        return this;
    }
}

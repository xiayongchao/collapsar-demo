package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.annotation.GetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/26 21:58
 */
public class GetMethodParser extends MethodParser {
    GetMethodParser(Method method, MethodDefinition methodDefinition) {
        super(method, methodDefinition);
    }

    @Override
    MethodParser parseMethodOperate() {
        if (!method.getName().startsWith(Operate.GET.getPrefix())) {
            throw new CollapsarException("[@GetOperate]注解方法[%s]请使用['%s']前缀",
                    methodFullName, Operate.GET.getPrefix());
        }
        cachesMethod.setOperate(Operate.GET);
        GetOperate setOperate = method.getDeclaredAnnotation(GetOperate.class);
        return this;
    }

    @Override
    MethodParser parseMethodParameter() {
        String nominateKey = method.getName().substring(Operate.GET.getPrefix().length());
        if (StringUtils.isEmpty(nominateKey)) {
            throw new CollapsarException("非法的[@GetOperate]方法[%s]命名,请提供Key的名称", methodFullName);
        }
        String[] parameterNames = nominateKey.split(METHOD_NAME_SEPARATOR);
        ParameterDefinition[] parameterDefinitions = getParameterDefinitions();
        if (ArrayUtils.isEmpty(parameterDefinitions)) {
            throw new CollapsarException("[@GetOperate]方法[%s]入参不能为空", methodFullName);
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.VALUE.equals(parameterDefinitions[i].getParamType())) {
                throw new CollapsarException("[@GetOperate]注解的方法[%s]的形参中不能有参数使用注解[@Value]",
                        methodFullName);
            }
        }
        cachesMethod.setParameterKeyBuilders(getParameterKeyBuilders(parameterNames, parameterDefinitions));
        return this;
    }

    @Override
    MethodParser parseMethodReturnType() {
        if (!method.getReturnType().equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s]",
                    methodFullName, methodDefinition.getTargetType().getName());
        }
        cachesMethod.setReturnType(method.getReturnType());
        return this;
    }
}

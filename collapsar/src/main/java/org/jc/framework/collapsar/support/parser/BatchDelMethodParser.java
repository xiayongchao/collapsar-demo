package org.jc.framework.collapsar.support.parser;

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
public class BatchDelMethodParser extends MethodParser {
    BatchDelMethodParser(Operate operate, Method method, MethodDefinition methodDefinition) {
        super(operate, method, methodDefinition);
    }

    @Override
    MethodParser parseMethodParameter() {
        String nominateKey = operate.removePrefix(method.getName(), cachesMethod.getModuleName(), methodDefinition.isMulti());
        if (StringUtils.isEmpty(nominateKey)) {
            throw new CollapsarException("非法的[%s]方法[%s]命名,请提供Key的名称", operate.getName(), methodFullName);
        }
        String[] parameterNames = nominateKey.split(METHOD_NAME_SEPARATOR);
        ParameterDefinition[] parameterDefinitions = getParameterDefinitions();
        if (ArrayUtils.isEmpty(parameterDefinitions)) {
            throw new CollapsarException("[%s]方法[%s]入参不能为空", operate.getName(), methodFullName);
        }

        ParameterDefinition parameterDefinition;
        Type parameterType;
        ParameterizedTypeImpl parameterizedType = null;
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.VALUE.equals((parameterDefinition = parameterDefinitions[i]).getParamType())) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中不能有参数使用注解[@Value]",
                        operate.getName(), methodFullName);
            }
            if ((parameterType = parameterDefinition.getType()) instanceof ParameterizedTypeImpl) {
                parameterizedType = (ParameterizedTypeImpl) parameterType;
                try {
                    (parameterizedType.getRawType()).asSubclass(List.class);
                } catch (ClassCastException e) {
                    throw new CollapsarException(e, "[%s]注解的方法[%s]不支持的参数类型[%s]",
                            operate.getName(), methodFullName, parameterType.getTypeName());
                }
            }
        }
        if (parameterizedType == null) {
            throw new CollapsarException("[%s]注解的方法[%s]必须提供的参数类型[%s]",
                    operate.getName(), methodFullName, List.class.getName());
        }
        cachesMethod.setParameterKeyBuilders(getParameterKeyBuilders(parameterNames, parameterDefinitions));
        return this;
    }

    @Override
    MethodParser parseMethodReturnType() {
        Type returnType = method.getAnnotatedReturnType().getType();
        if (!returnType.equals(Void.TYPE)) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s]",
                    methodFullName, Void.TYPE.getName());
        }
        cachesMethod.setReturnType(returnType);
        return this;
    }
}

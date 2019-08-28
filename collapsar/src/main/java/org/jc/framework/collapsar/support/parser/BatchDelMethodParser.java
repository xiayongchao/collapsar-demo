package org.jc.framework.collapsar.support.parser;

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
public class BatchDelMethodParser extends MethodParser {
    BatchDelMethodParser(Operate operate, Method method, MethodDefinition methodDefinition) {
        super(operate, method, methodDefinition);
    }

    @Override
    MethodParser parseMethodOperate() {
        if (!method.getName().startsWith(Operate.BATCH_DEL.getPrefix())) {
            throw new CollapsarException("[%s]注解方法[%s]请使用['%s']前缀",
                    Operate.BATCH_DEL.getName(), methodFullName, Operate.BATCH_DEL.getPrefix());
        }
        cachesMethod.setOperate(Operate.BATCH_DEL);
        return this;
    }

    @Override
    MethodParser parseMethodParameter() {
        String nominateKey = method.getName().substring(Operate.BATCH_DEL.getPrefix().length());
        if (StringUtils.isEmpty(nominateKey)) {
            throw new CollapsarException("非法的[%s]方法[%s]命名,请提供Key的名称", Operate.BATCH_DEL.getName(), methodFullName);
        }
        String[] parameterNames = nominateKey.split(METHOD_NAME_SEPARATOR);
        ParameterDefinition[] parameterDefinitions = getParameterDefinitions();
        if (ArrayUtils.isEmpty(parameterDefinitions)) {
            throw new CollapsarException("[%s]方法[%s]入参不能为空", Operate.BATCH_DEL.getName(), methodFullName);
        }
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.VALUE.equals(parameterDefinitions[i].getParamType())) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中不能有参数使用注解[@Value]",
                        Operate.BATCH_DEL.getName(), methodFullName);
            }
        }
        cachesMethod.setParameterKeyBuilders(getParameterKeyBuilders(parameterNames, parameterDefinitions));
        return this;
    }

    @Override
    MethodParser parseMethodReturnType() {
        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s]",
                    methodFullName, Void.TYPE.getName());
        }
        cachesMethod.setReturnType(method.getReturnType());
        return this;
    }
}

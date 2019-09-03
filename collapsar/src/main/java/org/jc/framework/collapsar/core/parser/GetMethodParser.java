package org.jc.framework.collapsar.core.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.core.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.core.invoker.GetMethodInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * ${@link org.jc.framework.collapsar.annotation.GetOperate}注解方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:58
 */
public class GetMethodParser extends MethodParser {
    private final GetMethodInvoker methodInvoker = new GetMethodInvoker();

    public GetMethodParser(Method method, MethodDefinition methodDefinition) {
        super(Operate.GET, method, methodDefinition);
    }

    @Override
    public MethodParser parseMethodParameterValue() {
        for (int i = 0; i < parameterDefinitions.length; i++) {
            if (ParamType.VALUE.equals(parameterDefinitions[i].getParamType())) {
                throw new CollapsarException("[%s]注解的方法[%s]的形参中不能有参数使用注解[@Value]",
                        operate.getName(), methodInvoker.getMethodFullName());
            }
        }
        return this;
    }

    @Override
    public MethodParser parseMethodReturnType() {
        Type returnType = method.getAnnotatedReturnType().getType();
        if (!methodDefinition.isMulti() && !returnType.equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s]",
                    get().getMethodFullName(), methodDefinition.getTargetType().getName());
        }
        methodInvoker.setReturnType(returnType);
        return this;
    }

    @Override
    public AbstractMethodInvoker get() {
        return methodInvoker;
    }
}

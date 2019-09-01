package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.DelMethodInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * ${@link org.jc.framework.collapsar.annotation.DelOperate}注解方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:58
 */
public class DelMethodParser extends MethodParser {
    private final DelMethodInvoker methodInvoker = new DelMethodInvoker();

    public DelMethodParser(Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        super(Operate.DEL, method, methodDefinition, penetrationsBean);
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
        if (!returnType.equals(Void.TYPE)) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s]",
                    methodInvoker.getMethodFullName(), Void.TYPE.getName());
        }
        return this;
    }

    @Override
    public AbstractMethodInvoker get() {
        return methodInvoker;
    }
}

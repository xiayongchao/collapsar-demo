package org.jc.framework.collapsar.core.parser;

import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.core.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.core.invoker.SetMethodInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * ${@link org.jc.framework.collapsar.annotation.SetOperate}注解方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:58
 */
public class SetMethodParser extends MethodParser {
    private final SetMethodInvoker methodInvoker = new SetMethodInvoker();

    public SetMethodParser(Method method, MethodDefinition methodDefinition) {
        super(Operate.SET, method, methodDefinition);
    }

    @Override
    public MethodParser parseMethodOperate() {
        super.parseMethodOperate();
        SetOperate setOperate = method.getDeclaredAnnotation(SetOperate.class);
        methodInvoker.setExpire(calcExpire(setOperate.expire(), setOperate.unit()));
        return this;
    }

    @Override
    public MethodParser parseMethodParameterValue() {
        int valueParameterIndex = getValueParameterIndex();
        methodInvoker.setValueParameterIndex(valueParameterIndex);
        Type valueParameterType = parameterDefinitions[valueParameterIndex].getType();
        if (!methodDefinition.isMulti() && !valueParameterType.equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的@Value形参类型请使用[%s]",
                    methodInvoker.getMethodFullName(), methodDefinition.getTargetType().getName());
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

package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.annotation.BatchSetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.BatchSetMethodInvoker;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jc
 * @date 2019/8/26 21:58
 */
public class BatchSetMethodParser extends MethodParser {
    private final BatchSetMethodInvoker methodInvoker = new BatchSetMethodInvoker();

    public BatchSetMethodParser(Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        super(Operate.BATCH_SET, method, methodDefinition, penetrationsBean);
    }

    @Override
    public MethodParser parseMethodOperate() {
        super.parseMethodOperate();
        BatchSetOperate batchSetOperate = method.getDeclaredAnnotation(BatchSetOperate.class);
        methodInvoker.setExpire(calcExpire(batchSetOperate.expire(), batchSetOperate.unit()));
        return this;
    }

    @Override
    public MethodParser parseMethodParameterValue() {
        int valueParameterIndex = getValueParameterIndex();
        methodInvoker.setValueParameterIndex(valueParameterIndex);
        Type valueParameterType = parameterDefinitions[valueParameterIndex].getType();
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) valueParameterType;
        try {
            (parameterizedType.getRawType()).asSubclass(List.class);
        } catch (ClassCastException e) {
            throw new CollapsarException("方法[%s]的@Value参数类型请使用[%s<%s>]或其实现类",
                    methodInvoker.getMethodFullName(), List.class.getName(), methodDefinition.getTargetType().getName());
        }
        if (!methodDefinition.isMulti() && !parameterizedType.getActualTypeArguments()[0].equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的@Value参数类型请使用[%s<%s>]或其实现类",
                    methodInvoker.getMethodFullName(), List.class.getName(), methodDefinition.getTargetType().getName());
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

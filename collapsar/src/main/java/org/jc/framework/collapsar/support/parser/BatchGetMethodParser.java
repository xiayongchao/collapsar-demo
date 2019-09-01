package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.annotation.BatchGetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.BatchGetMethodInvoker;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * ${@link org.jc.framework.collapsar.annotation.BatchGetOperate}注解方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:58
 */
public class BatchGetMethodParser extends MethodParser {
    private final BatchGetMethodInvoker methodInvoker = new BatchGetMethodInvoker();

    public BatchGetMethodParser(Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        super(Operate.BATCH_GET, method, methodDefinition, penetrationsBean);
    }

    @Override
    public MethodParser parseMethodParameterValue() {
        batchOperateMethodValueParameter();
        return this;
    }

    @Override
    public MethodParser parseMethodReturnType() {
        Type returnType = method.getAnnotatedReturnType().getType();
        if (!(returnType instanceof ParameterizedTypeImpl)) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s<%s>]或其实现类",
                    methodInvoker.getMethodFullName(), List.class.getName(), methodDefinition.getTargetType().getName());
        }
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) returnType;
        if (!parameterizedType.getRawType().equals(Optional.class)) {
            try {
                (parameterizedType.getRawType()).asSubclass(List.class);
            } catch (ClassCastException e) {
                throw new CollapsarException("方法[%s]的返回值类型请使用[%s<%s>]或其实现类",
                        methodInvoker.getMethodFullName(), List.class.getName(), methodDefinition.getTargetType().getName());
            }
        }
        if (!methodDefinition.isMulti() && !parameterizedType.getActualTypeArguments()[0].equals(methodDefinition.getTargetType())) {
            throw new CollapsarException("方法[%s]的返回值类型请使用[%s<%s>]或其实现类",
                    methodInvoker.getMethodFullName(), List.class.getName(), methodDefinition.getTargetType().getName());
        }
        methodInvoker.setReturnType(returnType);
        BatchGetOperate batchGetOperate = method.getDeclaredAnnotation(BatchGetOperate.class);
        methodInvoker.setImplType(batchGetOperate.implType());
        return this;
    }

    @Override
    public AbstractMethodInvoker get() {
        return methodInvoker;
    }
}

package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.BatchDelMethodInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * ${@link org.jc.framework.collapsar.annotation.BatchDelOperate}注解方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:58
 */
public class BatchDelMethodParser extends MethodParser {
    private final BatchDelMethodInvoker methodInvoker = new BatchDelMethodInvoker();

    public BatchDelMethodParser(Method method, MethodDefinition methodDefinition) {
        super(Operate.BATCH_DEL, method, methodDefinition);
    }

    @Override
    public MethodParser parseMethodParameterValue() {
        batchOperateMethodValueParameter();
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

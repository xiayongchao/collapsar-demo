package org.jc.framework.collapsar.core.handler;


import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.core.invoker.MethodInvoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class MethodParseHandler {
    private final Class<? extends Annotation> operateAnnotationClass;

    /**
     * 下一个处理器
     */
    private MethodParseHandler nextHandler;

    protected MethodParseHandler(Class<? extends Annotation> operateAnnotationClass) {
        this.operateAnnotationClass = operateAnnotationClass;
    }

    /**
     * 处理方法
     *
     * @param method
     * @param methodDefinition
     * @return
     */
    public MethodInvoker handleMethod(Method method, MethodDefinition methodDefinition) {
        if (!method.isAnnotationPresent(operateAnnotationClass)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, methodDefinition);
            }
        }
        int modifiers;
        if (Modifier.isFinal(modifiers = method.getModifiers())) {
            throw new CollapsarException("final方法[%s]上不允许使用注解[%s]", method.toString(), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        if (!Modifier.isPublic(modifiers)) {
            throw new CollapsarException("非public方法[%s]上不允许使用注解[%s]", method.toString(), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        if (Modifier.isNative(modifiers)) {
            throw new CollapsarException("native方法[%s]上不允许使用注解[%s]", method.toString(), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        if (Modifier.isStatic(modifiers)) {
            throw new CollapsarException("static方法[%s]上不允许使用注解[%s]", method.toString(), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        return getMethodInvoker(method, methodDefinition);
    }

    public abstract MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition);

    public MethodParseHandler getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(MethodParseHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    private static final MethodParseHandler METHOD_PARSE_HANDLER = initMethodParseHandler();

    private static MethodParseHandler initMethodParseHandler() {
        MethodParseHandler setMethodParseHandler = new SetMethodParseHandler();
        MethodParseHandler getMethodParseHandler = new GetMethodParseHandler();
        MethodParseHandler delMethodParseHandler = new DelMethodParseHandler();
        MethodParseHandler batchSetMethodParseHandler = new BatchSetMethodParseHandler();
        MethodParseHandler batchGetMethodParseHandler = new BatchGetMethodParseHandler();
        MethodParseHandler batchDelMethodParseHandler = new BatchDelMethodParseHandler();

        batchDelMethodParseHandler.setNextHandler(new OrdinaryMethodParseHandler());
        batchGetMethodParseHandler.setNextHandler(batchDelMethodParseHandler);
        batchSetMethodParseHandler.setNextHandler(batchGetMethodParseHandler);
        delMethodParseHandler.setNextHandler(batchSetMethodParseHandler);
        getMethodParseHandler.setNextHandler(delMethodParseHandler);
        setMethodParseHandler.setNextHandler(getMethodParseHandler);
        return setMethodParseHandler;
    }

    public static MethodInvoker parseHandleMethod(Method method, MethodDefinition methodDefinition) {
        return METHOD_PARSE_HANDLER.handleMethod(method, methodDefinition);
    }
}

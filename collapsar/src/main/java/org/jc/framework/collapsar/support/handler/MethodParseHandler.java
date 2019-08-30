package org.jc.framework.collapsar.support.handler;


import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;

import java.lang.reflect.Method;

public abstract class MethodParseHandler {
    /**
     * 下一个处理器
     */
    private MethodParseHandler nextHandler;

    /**
     * 处理方法
     *
     * @param method
     * @param methodDefinition
     * @return
     */
    public abstract MethodInvoker handleMethod(Method method, MethodDefinition methodDefinition, Object penetrationsBean);

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

        batchGetMethodParseHandler.setNextHandler(batchDelMethodParseHandler);
        batchSetMethodParseHandler.setNextHandler(batchGetMethodParseHandler);
        delMethodParseHandler.setNextHandler(batchSetMethodParseHandler);
        getMethodParseHandler.setNextHandler(delMethodParseHandler);
        setMethodParseHandler.setNextHandler(getMethodParseHandler);
        return setMethodParseHandler;
    }

    public static MethodInvoker parseHandleMethod(Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        return METHOD_PARSE_HANDLER.handleMethod(method, methodDefinition, penetrationsBean);
    }
}

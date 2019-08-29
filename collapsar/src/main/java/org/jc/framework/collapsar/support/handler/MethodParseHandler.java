package org.jc.framework.collapsar.support.handler;


import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.support.CachesMethod;

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
    public abstract CachesMethod handleMethod(Method method, MethodDefinition methodDefinition);

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

    public static CachesMethod parseHandleMethod(Method method, MethodDefinition methodDefinition) {
        return METHOD_PARSE_HANDLER.handleMethod(method, methodDefinition);
    }
}

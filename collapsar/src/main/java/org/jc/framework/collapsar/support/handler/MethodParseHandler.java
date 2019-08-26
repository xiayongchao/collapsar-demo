package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.definition.MethodDefinition;

import java.lang.reflect.Method;

public abstract class MethodParseHandler {
    //下一个处理器
    private MethodParseHandler nextHandler;

    //处理方法
    public abstract MethodDefinition handleMethod(Method method, Class<?> targetType);

    public MethodParseHandler getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(MethodParseHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
}

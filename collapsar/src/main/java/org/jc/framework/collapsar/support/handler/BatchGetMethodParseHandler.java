package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.BatchGetOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.BatchGetMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class BatchGetMethodParseHandler extends MethodParseHandler {
    protected BatchGetMethodParseHandler() {
        super(BatchGetOperate.class);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new BatchGetMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

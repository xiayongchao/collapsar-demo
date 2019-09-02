package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.BatchDelOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.BatchDelMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class BatchDelMethodParseHandler extends MethodParseHandler {
    protected BatchDelMethodParseHandler() {
        super(BatchDelOperate.class);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new BatchDelMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

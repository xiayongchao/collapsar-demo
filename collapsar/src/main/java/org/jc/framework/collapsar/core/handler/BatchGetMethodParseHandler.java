package org.jc.framework.collapsar.core.handler;

import org.jc.framework.collapsar.annotation.BatchGetOperate;
import org.jc.framework.collapsar.core.parser.BatchGetMethodParser;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.core.invoker.MethodInvoker;

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

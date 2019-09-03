package org.jc.framework.collapsar.core.handler;

import org.jc.framework.collapsar.annotation.BatchSetOperate;
import org.jc.framework.collapsar.core.parser.BatchSetMethodParser;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.core.invoker.MethodInvoker;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class BatchSetMethodParseHandler extends MethodParseHandler {
    protected BatchSetMethodParseHandler() {
        super(BatchSetOperate.class);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new BatchSetMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

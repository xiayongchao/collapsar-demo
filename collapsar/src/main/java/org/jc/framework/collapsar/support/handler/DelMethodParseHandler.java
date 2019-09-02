package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.DelOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.DelMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class DelMethodParseHandler extends MethodParseHandler {
    protected DelMethodParseHandler() {
        super(DelOperate.class);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new DelMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

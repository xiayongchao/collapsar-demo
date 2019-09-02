package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.OrdinaryMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class OrdinaryMethodParseHandler extends MethodParseHandler {
    protected OrdinaryMethodParseHandler() {
        super(null);
    }

    @Override
    public MethodInvoker handleMethod(Method method, MethodDefinition methodDefinition) {
        return getMethodInvoker(method, methodDefinition);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new OrdinaryMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.GetOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.GetMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class GetMethodParseHandler extends MethodParseHandler {
    protected GetMethodParseHandler() {
        super(GetOperate.class);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new GetMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

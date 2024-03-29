package org.jc.framework.collapsar.core.handler;

import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.core.invoker.MethodInvoker;
import org.jc.framework.collapsar.core.parser.SetMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class SetMethodParseHandler extends MethodParseHandler {
    protected SetMethodParseHandler() {
        super(SetOperate.class);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new SetMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

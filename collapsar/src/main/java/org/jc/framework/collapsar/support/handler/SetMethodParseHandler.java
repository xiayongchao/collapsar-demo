package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.OrdinaryMethodParser;
import org.jc.framework.collapsar.support.parser.SetMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class SetMethodParseHandler extends MethodParseHandler {
    @Override
    public MethodInvoker handleMethod(Method method, MethodDefinition methodDefinition) {
        if (!method.isAnnotationPresent(SetOperate.class)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, methodDefinition);
            }
            return new OrdinaryMethodParser(method, methodDefinition).getMethodInvoker();
        }
        return new SetMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

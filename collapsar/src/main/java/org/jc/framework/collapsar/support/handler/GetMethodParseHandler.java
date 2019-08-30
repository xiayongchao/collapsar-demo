package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.GetOperate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.support.parser.GetMethodParser;
import org.jc.framework.collapsar.support.parser.OrdinaryMethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class GetMethodParseHandler extends MethodParseHandler {
    @Override
    public MethodInvoker handleMethod(Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        if (!method.isAnnotationPresent(GetOperate.class)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, methodDefinition, penetrationsBean);
            }
            return new OrdinaryMethodParser(method, methodDefinition, penetrationsBean).getMethodInvoker();
        }
        return new GetMethodParser(method, methodDefinition, penetrationsBean).getMethodInvoker();
    }
}

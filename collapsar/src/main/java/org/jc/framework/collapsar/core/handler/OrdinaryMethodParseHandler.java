package org.jc.framework.collapsar.core.handler;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.core.invoker.MethodInvoker;
import org.jc.framework.collapsar.core.parser.OrdinaryMethodParser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
        if (Modifier.isAbstract(method.getModifiers())) {
            throw new CollapsarException("abstract方法[%s]请加上[%s]注解", method.toString(), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        return getMethodInvoker(method, methodDefinition);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method, MethodDefinition methodDefinition) {
        return new OrdinaryMethodParser(method, methodDefinition).getMethodInvoker();
    }
}

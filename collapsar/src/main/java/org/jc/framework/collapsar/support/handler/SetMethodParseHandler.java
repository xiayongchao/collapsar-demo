package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.parser.MethodParser;
import org.jc.framework.collapsar.util.Methods;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class SetMethodParseHandler extends MethodParseHandler {
    @Override
    public MethodDefinition handleMethod(Method method, Class<?> targetType) {
        if (!method.isAnnotationPresent(SetOperate.class)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, targetType);
            }
            throw new CollapsarException("请在方法[%s]上使用注解[@SetOperate/@GetOperate/@DelOperate]",
                    Methods.getMethodFullName(method));
        }
        return MethodParser.parseMethod(Operate.SET, method, targetType);
    }
}

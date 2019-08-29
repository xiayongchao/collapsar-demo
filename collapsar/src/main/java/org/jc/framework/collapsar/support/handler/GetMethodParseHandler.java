package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.GetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.support.CachesMethod;
import org.jc.framework.collapsar.support.parser.MethodParser;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class GetMethodParseHandler extends MethodParseHandler {
    @Override
    public CachesMethod handleMethod(Method method, MethodDefinition methodDefinition) {
        if (!method.isAnnotationPresent(GetOperate.class)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, methodDefinition);
            }
            return null;
        }
        return MethodParser.parseMethod(Operate.GET, method, methodDefinition);
    }
}

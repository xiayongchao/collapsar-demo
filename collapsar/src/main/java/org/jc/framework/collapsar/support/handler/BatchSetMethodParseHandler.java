package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.BatchSetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.CachesMethod;
import org.jc.framework.collapsar.support.parser.MethodParser;
import org.jc.framework.collapsar.util.Methods;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class BatchSetMethodParseHandler extends MethodParseHandler {
    @Override
    public CachesMethod handleMethod(Method method, MethodDefinition methodDefinition) {
        if (!method.isAnnotationPresent(BatchSetOperate.class)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, methodDefinition);
            }
            throw new CollapsarException("请在方法[%s]上使用注解[%s]",
                    Methods.getMethodFullName(method), Operate.ENABLE_METHOD_ANNOTATIONS_STRING);
        }
        return MethodParser.parseMethod(Operate.BATCH_SET, method, methodDefinition);
    }
}

package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.ExpireCalculator;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/25 21:11
 */
public class SetMethodParseHandler extends MethodParseHandler {
    @Override
    public boolean handleMethod(Method method, MethodDefinition methodDefinition) {
        if (!method.isAnnotationPresent(SetOperate.class)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleMethod(method, methodDefinition);
            }
            throw new CollapsarException("请在方法[%s#%s]上使用注解[@SetOperate/@GetOperate/@DelOperate]",
                    methodDefinition.getClassName(), methodDefinition.getMethodName());
        }
        if (!method.getName().startsWith(Operate.SET.getPrefix())) {
            throw new CollapsarException("[@SetOperate]注解方法[%s#%s]请使用['%s']前缀",
                    methodDefinition.getClassName(), methodDefinition.getMethodName(), Operate.SET.getPrefix());
        }
        methodDefinition.setOperate(Operate.SET);
        SetOperate setOperate = method.getDeclaredAnnotation(SetOperate.class);
        methodDefinition.setExpire(ExpireCalculator.calc(setOperate.expire(), setOperate.unit()));

        return false;
    }
}

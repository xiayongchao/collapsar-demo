package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.invoker.AbstractMethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.MethodInvoker;
import org.jc.framework.collapsar.proxy.invoker.OrdinaryMethodInvoker;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/26 21:58
 */
public class OrdinaryMethodParser extends MethodParser {
    private final OrdinaryMethodInvoker methodInvoker = new OrdinaryMethodInvoker();

    public OrdinaryMethodParser(Method method, MethodDefinition methodDefinition, Object penetrationsBean) {
        super(Operate.NONE, method, methodDefinition, penetrationsBean);
    }

    @Override
    public MethodInvoker getMethodInvoker() {
        Method penetrationsMethod;
        try {
            penetrationsMethod = penetrationsBean != null ? penetrationsBean.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes()) : null;
        } catch (NoSuchMethodException e) {
            throw new CollapsarException(e, "获取@Penetrations Bean[%s]方法[%s]失败", penetrationsBean.getClass().getName(), method.getName());
        }
        methodInvoker.setPenetrationsBean(penetrationsBean);
        methodInvoker.setPenetrationsMethod(penetrationsMethod);
        return methodInvoker;
    }

    @Override
    protected AbstractMethodInvoker get() {
        return null;
    }
}

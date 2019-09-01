package org.jc.framework.collapsar.proxy.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public abstract class PenetrationsMethodInvoker implements MethodInvoker {
    protected Object penetrationsBean;
    protected Method penetrationsMethod;

    protected Object invokePenetrationMethod(Object self, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (proceed != null) {
            return proceed.invoke(self, args);
        }
        if (penetrationsBean == null || penetrationsMethod == null) {
            return null;
        }
        return penetrationsMethod.invoke(penetrationsBean, args);
    }

    public Object getPenetrationsBean() {
        return penetrationsBean;
    }

    public void setPenetrationsBean(Object penetrationsBean) {
        this.penetrationsBean = penetrationsBean;
    }

    public Method getPenetrationsMethod() {
        return penetrationsMethod;
    }

    public void setPenetrationsMethod(Method penetrationsMethod) {
        this.penetrationsMethod = penetrationsMethod;
    }
}

package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/22 22:50
 */
public class CachesMethodSupporter {
    private final CachesMethod cachesMethod;
    private final Object penetrationsBean;
    private final Method penetrationsMethod;

    public CachesMethodSupporter(CachesMethod cachesMethod, Object penetrationsBean, Method penetrationsMethod) {
        this.cachesMethod = cachesMethod;
        this.penetrationsBean = penetrationsBean;
        this.penetrationsMethod = penetrationsMethod;
    }

    public Object invokePenetrationMethod(Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (penetrationsBean == null || penetrationsMethod == null) {
            return null;
        }
        return penetrationsMethod.invoke(penetrationsBean, args);
    }

    public CachesMethod getCachesMethod() {
        return cachesMethod;
    }

    public String getMethodFullName() {
        return cachesMethod.getMethodFullName();
    }

    public Operate getOperate() {
        return cachesMethod == null ? Operate.NONE : cachesMethod.getOperate();
    }
}

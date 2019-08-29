package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/22 22:50
 */
public class CachesMethodWrapper {
    private final CachesMethod cachesMethod;

    public CachesMethodWrapper(CachesMethod cachesMethod, Object penetrationsBean, Method penetrationsMethod) {
        cachesMethod.setPenetrationsBean(penetrationsBean);
        cachesMethod.setPenetrationsMethod(penetrationsMethod);
        this.cachesMethod = cachesMethod;
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

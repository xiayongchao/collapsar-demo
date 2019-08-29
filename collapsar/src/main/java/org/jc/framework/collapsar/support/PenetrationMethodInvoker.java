package org.jc.framework.collapsar.support;

import java.lang.reflect.InvocationTargetException;

public interface PenetrationMethodInvoker {
    Object invokePenetrationMethod(Object[] args) throws InvocationTargetException, IllegalAccessException;
}

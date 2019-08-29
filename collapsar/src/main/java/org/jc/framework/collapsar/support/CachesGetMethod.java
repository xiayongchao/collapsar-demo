package org.jc.framework.collapsar.support;

import java.lang.reflect.Type;

public interface CachesGetMethod extends PenetrationMethodInvoker {
    String generateKey(Object[] args);

    Type getReturnType();
}

package org.jc.framework.collapsar.support;

public interface CachesSetMethod extends PenetrationMethodInvoker {

    String generateKey(Object[] args);

    Object selectValueParameter(Object[] args);

    long getExpire();
}

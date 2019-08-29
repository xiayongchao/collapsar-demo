package org.jc.framework.collapsar.support;

public interface CachesDelMethod extends PenetrationMethodInvoker {
    String generateKey(Object[] args);
}

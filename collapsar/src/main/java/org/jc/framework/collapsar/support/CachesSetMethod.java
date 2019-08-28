package org.jc.framework.collapsar.support;

public interface CachesSetMethod {
    long getExpire();

    String generateKey(Object[] args);

    Object selectValueParameter(Object[] args);
}

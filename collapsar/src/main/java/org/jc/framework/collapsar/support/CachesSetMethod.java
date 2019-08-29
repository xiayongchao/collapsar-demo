package org.jc.framework.collapsar.support;

public interface CachesSetMethod {

    String generateKey(Object[] args);

    Object selectValueParameter(Object[] args);

    long getExpire();
}

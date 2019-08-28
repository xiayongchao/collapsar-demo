package org.jc.framework.collapsar.support;

public interface CachesGetMethod {
    String generateKey(Object[] args);

    Class<?> getReturnType();
}

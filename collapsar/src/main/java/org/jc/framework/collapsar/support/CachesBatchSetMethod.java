package org.jc.framework.collapsar.support;

public interface CachesBatchSetMethod {
    String generateKey(Object[] args);

    Object[] filterArgs(int i, Object[] args);

    int calcListSize(Object[] args);

    Object selectValueParameter(Object[] args);

    Object selectValueParameter(int i, Object[] args);

    long getExpire();
}

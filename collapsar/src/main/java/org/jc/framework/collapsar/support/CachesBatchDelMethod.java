package org.jc.framework.collapsar.support;

public interface CachesBatchDelMethod extends PenetrationMethodInvoker {
    String generateKey(Object[] args);

    Object[] filterArgs(int i, Object[] args);

    int calcListSize(Object[] args);
}

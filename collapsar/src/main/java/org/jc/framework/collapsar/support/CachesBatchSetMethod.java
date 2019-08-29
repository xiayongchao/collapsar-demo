package org.jc.framework.collapsar.support;

public interface CachesBatchSetMethod extends PenetrationMethodInvoker {
    String generateKey(Object[] args);

    Object[] filterArgs(int i, Object[] args);

    int calcListSize(Object[] args);

    Object selectValueParameter(int i, Object[] args);

    long getExpire();
}

package org.jc.framework.collapsar.support;

import java.lang.reflect.Type;
import java.util.List;

public interface CachesBatchGetMethod {
    String generateKey(Object[] args);

    Object[] filterArgs(int i, Object[] args);

    Object[] filterArgs(List<Integer> indexList, Object[] args);

    int calcListSize(Object[] args);

    Class<? extends List> getImplType();

    Type getReturnType();
}

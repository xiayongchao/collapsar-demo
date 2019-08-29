package org.jc.framework.collapsar.support;

import java.lang.reflect.Type;

public interface CachesGetMethod {
    String generateKey(Object[] args);

    Type getReturnType();
}

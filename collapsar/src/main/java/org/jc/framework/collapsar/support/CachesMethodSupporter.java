package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;

/**
 * @author jc
 * @date 2019/8/22 22:50
 */
public class CachesMethodSupporter {
    private final CachesMethod cachesMethod;

    public CachesMethodSupporter(CachesMethod cachesMethod) {
        this.cachesMethod = cachesMethod;
    }

    public CachesMethod getCachesMethod() {
        return cachesMethod;
    }

    public String getMethodFullName() {
        return cachesMethod.getMethodFullName();
    }

    public Operate getOperate() {
        return cachesMethod.getOperate();
    }

    public String buildCacheKey(Object[] args) {
        return cacheKeyGenerator.buildCacheKey(args, methodDefinition.getParamDefinitions());
    }
}

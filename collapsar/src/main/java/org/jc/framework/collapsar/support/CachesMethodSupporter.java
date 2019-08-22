package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.support.CacheKeyGenerator;

/**
 * @author jc
 * @date 2019/8/22 22:50
 */
public class CachesMethodSupporter {
    private final String methodFullName;
    private final CacheKeyGenerator cacheKeyGenerator;
    private final MethodDefinition methodDefinition;

    public CachesMethodSupporter(String methodFullName, CacheKeyGenerator cacheKeyGenerator, MethodDefinition methodDefinition) {
        this.methodFullName = methodFullName;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.methodDefinition = methodDefinition;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public Operate getOperate() {
        return methodDefinition.getOperate();
    }

    public long getExpire() {
        return methodDefinition.getExpire();
    }

    public Class<?> getReturnType() {
        return methodDefinition.getReturnType();
    }

    public Object selectValueParameter(Object[] args) {
        return args[methodDefinition.getValueParameterIndex()];
    }

    public String buildCacheKey(Object[] args) {
        return cacheKeyGenerator.buildCacheKey(args, methodDefinition.getParamDefinitions());
    }
}

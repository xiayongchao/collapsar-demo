package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;

/**
 * @author jc
 * @date 2019/8/28 0:26
 */
public class CachesMethod implements CachesSetMethod, CachesGetMethod, CachesDelMethod {
    private final String cacheKeyTemplate;
    private final String methodFullName;
    private Operate operate;
    private ParameterKeyBuilder[] parameterKeyBuilders;
    private Class<?> returnType;
    /**
     * 缓存过期时间
     */
    private long expire = 0;
    private int valueParameterIndex = -1;

    public CachesMethod(String projectName, String moduleName, String connector, String methodFullName) {
        this.cacheKeyTemplate = projectName + connector + moduleName + connector + "%s_%s";
        this.methodFullName = methodFullName;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }

    public ParameterKeyBuilder[] getParameterKeyBuilders() {
        return parameterKeyBuilders;
    }

    public void setParameterKeyBuilders(ParameterKeyBuilder[] parameterKeyBuilders) {
        this.parameterKeyBuilders = parameterKeyBuilders;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public int getValueParameterIndex() {
        return valueParameterIndex;
    }

    public void setValueParameterIndex(int valueParameterIndex) {
        this.valueParameterIndex = valueParameterIndex;
    }
}

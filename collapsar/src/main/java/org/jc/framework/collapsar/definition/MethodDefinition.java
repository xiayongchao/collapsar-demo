package org.jc.framework.collapsar.definition;

import org.jc.framework.collapsar.constant.Operate;

/**
 * @author xiayc
 * @date 2019/3/29
 */
public class MethodDefinition {
    private String className;
    private String methodName;
    private Operate operate;
    private ParamDefinition[] paramDefinitions;
    private Class<?> returnType;
    /**
     * 缓存过期时间
     */
    private long expire = 0;
    private int valueParameterIndex = -1;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }

    public ParamDefinition[] getParamDefinitions() {
        return paramDefinitions;
    }

    public void setParamDefinitions(ParamDefinition[] paramDefinitions) {
        this.paramDefinitions = paramDefinitions;
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

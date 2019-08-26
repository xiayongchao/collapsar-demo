package org.jc.framework.collapsar.definition;

import org.jc.framework.collapsar.constant.ParamType;

import java.lang.reflect.Type;

/**
 * @author xiayc
 * @date 2019/3/29
 */
public class ParameterDefinition {
    private Type type;
    private String[] names;
    private ParamType paramType;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }
}

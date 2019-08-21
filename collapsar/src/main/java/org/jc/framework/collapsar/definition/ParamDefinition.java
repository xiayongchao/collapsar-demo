package org.jc.framework.collapsar.definition;

import java.lang.reflect.Type;

/**
 * @author xiayc
 * @date 2019/3/29
 */
public class ParamDefinition {
    private Type type;
    private String name;
    private boolean isValue;
    private int realOrder;
    private int keyOrder;

    public ParamDefinition(Type type, String name, boolean isValue, int realOrder, int keyOrder) {
        this.type = type;
        this.name = name;
        this.isValue = isValue;
        this.realOrder = realOrder;
        this.keyOrder = keyOrder;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValue() {
        return isValue;
    }

    public void setValue(boolean value) {
        isValue = value;
    }

    public int getRealOrder() {
        return realOrder;
    }

    public void setRealOrder(int realOrder) {
        this.realOrder = realOrder;
    }

    public int getKeyOrder() {
        return keyOrder;
    }

    public void setKeyOrder(int keyOrder) {
        this.keyOrder = keyOrder;
    }
}

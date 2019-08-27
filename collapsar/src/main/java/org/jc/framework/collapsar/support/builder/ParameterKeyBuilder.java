package org.jc.framework.collapsar.support.builder;

/**
 * @author xiayc
 * @date 2019/8/27
 */
public abstract class ParameterKeyBuilder {
    protected int index;
    protected String name;

    public ParameterKeyBuilder(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public final String buildKey(Object[] args) {
        return this.buildKey(args[index]);
    }

    protected abstract String buildKey(Object arg);
}

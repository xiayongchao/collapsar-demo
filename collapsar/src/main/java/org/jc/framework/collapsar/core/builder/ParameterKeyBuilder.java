package org.jc.framework.collapsar.core.builder;

/**
 * @author xiayc
 * @date 2019/8/27
 */
public abstract class ParameterKeyBuilder {
    protected final int index;
    protected final String name;
    protected final boolean isBatch;

    public ParameterKeyBuilder(int index, String name, boolean isBatch) {
        this.index = index;
        this.name = name;
        this.isBatch = isBatch;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isBatch() {
        return isBatch;
    }

    public final String buildKey(Object[] args) {
        return this.buildKey(args[index]);
    }

    protected abstract String buildKey(Object arg);
}

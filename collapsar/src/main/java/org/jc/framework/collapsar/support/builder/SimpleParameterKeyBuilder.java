package org.jc.framework.collapsar.support.builder;

/**
 * @author xiayc
 * @date 2019/8/27
 */
public class SimpleParameterKeyBuilder extends ParameterKeyBuilder {
    public SimpleParameterKeyBuilder(int index, String name) {
        super(index, name);
    }

    @Override
    protected String buildKey(Object arg) {
        return String.valueOf(arg);
    }
}
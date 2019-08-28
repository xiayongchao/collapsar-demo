package org.jc.framework.collapsar.support.builder;

import org.jc.framework.collapsar.exception.CollapsarException;

import java.lang.reflect.Field;

/**
 * @author xiayc
 * @date 2019/8/27
 */
public class ReflectParameterKeyBuilder extends ParameterKeyBuilder {
    private final Field field;

    public ReflectParameterKeyBuilder(int index, String name, boolean isBatch, Field field) {
        super(index, name, isBatch);
        this.field = field;
    }

    @Override
    protected String buildKey(Object arg) {
        try {
            return String.valueOf(field.get(arg));
        } catch (IllegalAccessException e) {
            throw new CollapsarException(e, "构建缓存参数Key名称失败");
        }
    }
}

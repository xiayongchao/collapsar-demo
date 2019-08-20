package org.jc.framework.collapsar.annotation;

import org.jc.framework.collapsar.constant.Constants;

import java.lang.annotation.*;

/**
 * @author xiayc
 * @date 2019/3/25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Caches {
    /**
     * Bean名称，用于Spring注入
     *
     * @return
     */
    String value() default Constants.EMPTY_STRING;

    /**
     * 模块名/缓存二级结构名称
     *
     * @return
     */
    String moduleName() default Constants.NULL_STRING;

    /**
     * 缓存对象的类型
     *
     * @return
     */
    Class<?> targetType();
}

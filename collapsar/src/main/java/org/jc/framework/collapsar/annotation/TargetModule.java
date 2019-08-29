package org.jc.framework.collapsar.annotation;

import org.jc.framework.collapsar.util.Strings;

import java.lang.annotation.*;

/**
 * @author xiayc
 * @date 2019/8/29
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetModule {
    /**
     * 模块名/缓存二级结构名称
     *
     * @return
     */
    String moduleName() default Strings.NULL_STRING;

    /**
     * 缓存对象的类型
     *
     * @return
     */
    Class<?> targetType() default Null.class;
}

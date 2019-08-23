package org.jc.framework.collapsar.annotation;

import org.jc.framework.collapsar.util.Strings;

import java.lang.annotation.*;

/**
 * @author xiayc
 * @date 2019/8/23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiCaches {
    /**
     * Bean名称，用于Spring注入
     *
     * @return
     */
    String value() default Strings.EMPTY_STRING;

    /**
     * 模块名/缓存二级结构名称
     *
     * @return
     */
    String moduleName() default Strings.NULL_STRING;
}

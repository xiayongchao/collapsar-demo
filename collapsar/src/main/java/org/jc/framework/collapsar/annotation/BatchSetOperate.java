package org.jc.framework.collapsar.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 标识缓存Set操作
 *
 * @author xiayc
 * @date 2019/3/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BatchSetOperate {
    /**
     * 缓存过期时间，单位是${@link #unit()}
     * 默认为永远不过期
     *
     * @return
     */
    long expire() default 0;

    /**
     * 过期时间${@link #expire()}的单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}

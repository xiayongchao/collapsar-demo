package org.jc.framework.collapsar.annotation;

import java.lang.annotation.*;

/**
 * 缓存Value的注解
 *
 * @author xiayc
 * @date 2018/10/29
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
}

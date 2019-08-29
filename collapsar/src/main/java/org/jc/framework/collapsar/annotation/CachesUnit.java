package org.jc.framework.collapsar.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 缓存单元
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CachesUnit {
    String value() default "";
}

package org.jc.framework.collapsar.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Keys {
    String[] value() default {};
}

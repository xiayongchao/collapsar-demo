package org.jc.framework.collapsar.annotation;

import java.lang.annotation.*;

/**
 * 缓存穿透组件注解
 *
 * @author xiayc
 * @date 2019/8/28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Penetrations {
}

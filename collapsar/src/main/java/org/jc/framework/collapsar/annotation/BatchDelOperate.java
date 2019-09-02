package org.jc.framework.collapsar.annotation;

import java.lang.annotation.*;

/**
 * 标识缓存Del操作
 *
 * @author xiayc
 * @date 2019/3/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CacheOperate
public @interface BatchDelOperate {
}

package org.jc.framework.collapsar.annotation;

import java.lang.annotation.*;
import java.util.List;

/**
 * 标识缓存Get操作
 *
 * @author xiayc
 * @date 2019/3/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CacheOperate
public @interface BatchGetOperate {
    Class<? extends List> implType();
}

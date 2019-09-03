package org.jc.framework.collapsar.annotation;

import org.jc.framework.collapsar.util.Strings;

import java.lang.annotation.*;

/**
 * @author xiayc
 * @date 2019/8/29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollapsarComponentScan {
    Rule[] scope();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface Rule {
        /**
         * 项目名称/缓存一级结构名称
         *
         * @return
         */
        String projectName() default Strings.EMPTY_STRING;

        /**
         * 扫描包名列表
         *
         * @return
         */
        String[] basePackages() default {};

        /**
         * 匹配模式
         *
         * @return
         */
        String resourcePattern() default "**/*.class";

        /**
         * 缓存Key直接的连接符
         *
         * @return
         */
        String connector() default ":";
    }
}

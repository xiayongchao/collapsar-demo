package org.jc.framework.collapsar.constant;

import org.springframework.util.StringUtils;

/**
 * @author xiayc
 * @date 2019/3/27
 */
public enum Operate {
    SET("setBy"), GET("getBy"), DEL("delBy");
    private String prefix;

    Operate(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isStartOf(String string) {
        return StringUtils.hasLength(string) && string.startsWith(this.prefix);
    }

    public String removePrefix(String string) {
        if (isStartOf(string)) {
            string = string.substring(this.prefix.length(), string.length());
        }
        return string;
    }
}

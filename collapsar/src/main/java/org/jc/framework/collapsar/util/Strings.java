package org.jc.framework.collapsar.util;

import org.springframework.util.StringUtils;

/**
 * @author xiayc
 * @date 2019/3/29
 */
public final class Strings {
    public final static String EMPTY_STRING = "";
    public final static String NULL_STRING = "\u0000";

    /**
     * 首字母小写
     *
     * @param string
     * @return
     */
    public static String standingInitialLowercase(String string) {
        if (StringUtils.hasLength(string)) {
            char[] cs = string.toCharArray();
            cs[0] = (char) (cs[0] + 32);
            return String.valueOf(cs);
        }
        return string;
    }
}

package org.jc.framework.collapsar.util;

/**
 * @author xiayc
 * @date 2019/3/26
 */
public final class ArrayUtils {
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }
}

package org.jc.framework.collapsar.util;

import java.lang.reflect.Method;

/**
 * @author jc
 * @date 2019/8/26 22:19
 */
public class Methods {
    public static String getMethodFullName(Method method) {
        if (method == null) {
            return null;
        }
        return String.format("%s#%s", method.getDeclaringClass().getName(), method.getName());
    }
}

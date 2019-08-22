package org.jc.framework.collapsar.core;

import java.util.concurrent.TimeUnit;

/**
 * @author jc
 * @date 2019/8/22 23:14
 */
public class ExpireCalculator {
    public static long calc(long expire, TimeUnit unit) {
        return unit.convert(expire, TimeUnit.MILLISECONDS);
    }
}

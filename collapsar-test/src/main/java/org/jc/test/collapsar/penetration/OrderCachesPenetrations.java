package org.jc.test.collapsar.penetration;

import org.jc.framework.collapsar.annotation.Penetrations;
import org.jc.test.collapsar.caches.OrderCaches;

/**
 * @author xiayc
 * @date 2019/8/28
 */
//@Penetrations
public class OrderCachesPenetrations extends OrderCaches {
    @Override
    public void delById(Long id) {
        System.out.println("OrderCachesPenetrations#delById");
    }
}

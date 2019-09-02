package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.annotation.DelOperate;
import org.jc.framework.collapsar.annotation.Key;
import org.jc.test.collapsar.modal.Order;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jc
 * @date 2019/8/31 22:26
 */
@Caches(targetType = Order.class)
public abstract class OrderCaches {
    @Autowired
    private CommonCaches commonCaches;

    @DelOperate
    public void delById(@Key("id") Long id) {
        System.out.println("我是抽象类的delById");
        commonCaches.delUserById(23L);
    }

    public void getById(Long id) {
        System.out.println("我是抽象类的getById");
        this.getByIdxxx(3242L);
    }

    private void getByIdxxx(Long id) {
        System.out.println("我是抽象类的getByIdxxx");
    }

    protected void getByIdyyyy(Long id) {
        System.out.println("我是抽象类的getByIdyyyy");
    }

//    public abstract void getByUserId(Long id);

//    protected abstract void getByMoney(Long id);
}

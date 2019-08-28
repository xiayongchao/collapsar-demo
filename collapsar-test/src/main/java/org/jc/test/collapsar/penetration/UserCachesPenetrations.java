package org.jc.test.collapsar.penetration;

import org.jc.framework.collapsar.annotation.Key;
import org.jc.framework.collapsar.annotation.Penetrations;
import org.jc.framework.collapsar.annotation.Value;
import org.jc.test.collapsar.caches.UserCaches;
import org.jc.test.collapsar.modal.Order;
import org.jc.test.collapsar.modal.User;

import java.util.List;

/**
 * @author xiayc
 * @date 2019/8/28
 */
@Penetrations
public class UserCachesPenetrations implements UserCaches {
    @Override
    public void setById(@Key("id") Long id, @Value User user) {
        System.out.println("发大水时发大水");
    }

    //    @Override
    public void setByIdxxx(@Value User user) {
        System.out.println("48122222222");
    }

    @Override
    public void delById(@Key("id") Long id) {
        System.out.println("让哥哥稍等发");
    }

    @Override
    public void setByIdAndUserName(@Value User user) {
        System.out.println("哭也可以痛苦");
    }

    @Override
    public User getById(@Key("id") Long id) {
        User u = new User();
        u.setId(1L);
        u.setUserName("xyc");
        return u;
    }

    @Override
    public void batchDelByIdAndUserNameAndMoney(Long id, List<Order> orderList, List<User> userList) {
        System.out.println("batchDelByIdAndUserNameAndMoney");
    }

    @Override
    public void batchDelById(List<Long> idList) {
        System.out.println("batchDelById");
    }

    @Override
    public List<User> batchGetById(List<Long> idList) {
        System.out.println("batchGetById");
        return null;
    }

    @Override
    public void batchGetByIdAndUserNameAndMoney(Long id, List<Order> orderList, List<User> userList) {
        System.out.println("batchGetByIdAndUserNameAndMoney");
    }

    //    @Override
//    public void batchGetById(@Key("id") List<Long> idList) {
//
//    }

//    @Override
//    public void batchSetById(@Value List<User> user) {
//
//    }
}

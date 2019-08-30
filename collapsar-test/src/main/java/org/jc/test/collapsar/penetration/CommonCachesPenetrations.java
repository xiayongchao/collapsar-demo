package org.jc.test.collapsar.penetration;

import org.jc.framework.collapsar.annotation.Key;
import org.jc.framework.collapsar.annotation.Keys;
import org.jc.framework.collapsar.annotation.Penetrations;
import org.jc.framework.collapsar.annotation.Value;
import org.jc.test.collapsar.common.CommonCaches;
import org.jc.test.collapsar.modal.Order;
import org.jc.test.collapsar.modal.User;

import java.util.List;

/**
 * @author xiayc
 * @date 2019/8/28
 */
@Penetrations
public class CommonCachesPenetrations implements CommonCaches {
    @Override
    public void delUserById(@Key("id") Long id) {

    }

    @Override
    public void setXycById(@Key("id") Long id, @Value User user) {

    }

    @Override
    public Order getUserById(@Keys User user) {
        return null;
    }

    @Override
    public void batchDelUserByIdAndUserNameAndMoney(@Key("id") Long id, @Keys("money") List<Order> orderList, @Keys("userName") List<User> userList) {

    }

    @Override
    public void batchDelUserById(@Key("id") List<Long> idList) {

    }

    @Override
    public List<User> batchGetUserById(@Key("id") List<Long> idList) {
        return null;
    }

    @Override
    public List<User> batchGetUserByIdAndUserNameAndMoney(@Key("id") Long id, @Keys("money") List<Order> orderList, @Keys("userName") List<User> userList) {
        return null;
    }

    @Override
    public void batchSetUserById(@Value List<User> user) {

    }
}

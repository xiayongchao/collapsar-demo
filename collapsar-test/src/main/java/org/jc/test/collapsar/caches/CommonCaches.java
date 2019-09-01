package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.*;
import org.jc.test.collapsar.modal.Order;
import org.jc.test.collapsar.modal.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiayc
 * @date 2019/8/23
 */
@MultiCaches
public interface CommonCaches {
    @DelOperate
    void delUserById(@Key("id") Long id);

    @SetOperate
    void setXycById(@Key("id") Long id, @Value User user);

    @GetOperate
    Order getUserById(@Keys User user);

    @BatchDelOperate
    void batchDelUserByIdAndUserNameAndMoney(@Key("id") Long id, @Keys("money") List<Order> orderList, @Keys("userName") List<User> userList);

    @BatchDelOperate
    void batchDelUserById(@Key("id") List<Long> idList);

    @BatchGetOperate(implType = ArrayList.class)
    List<User> batchGetUserById(@Key("id") List<Long> idList);

    @BatchGetOperate(implType = ArrayList.class)
    List<User> batchGetUserByIdAndUserNameAndMoney(@Key("id") Long id, @Keys("money") List<Order> orderList, @Keys("userName") List<User> userList);

    @BatchSetOperate
    void batchSetUserById(@Value List<User> user);
}

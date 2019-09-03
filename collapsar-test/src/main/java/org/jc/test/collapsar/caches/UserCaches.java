package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.*;
import org.jc.framework.collapsar.core.parser.Optional;
import org.jc.test.collapsar.modal.Order;
import org.jc.test.collapsar.modal.User;

import java.util.ArrayList;
import java.util.List;

@Caches(targetType = User.class)
public interface UserCaches {
    @SetOperate
    void setById(@Key("id") Long id, @Value User user);

//    @SetOperate
//    void setById(User user);

    @DelOperate
    void delById(@Key("id") Long id);

    @BatchDelOperate
    void batchDelByIdAndUserNameAndMoney(@Key("id") Long id, @Keys("money") List<Order> orderList, @Keys("userName") List<User> userList);

    @BatchDelOperate
    void batchDelById(@Key("id") List<Long> idList);

    @BatchGetOperate(implType = ArrayList.class)
    Optional<User> batchGetById(@Key("id") List<Long> idList);

    @BatchGetOperate(implType = ArrayList.class)
    List<User> batchGetByIdAndUserNameAndMoney(@Key("id") Long id, @Keys("money") List<Order> orderList, @Keys("userName") List<User> userList);

    @BatchSetOperate
    void batchSetById(@Value List<User> user);

    @SetOperate
    void setByIdAndUserName(@Value User user);

    @GetOperate
    User getById(@Key("id") Long id);
}

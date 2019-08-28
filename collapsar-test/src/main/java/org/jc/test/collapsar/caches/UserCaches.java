package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.*;
import org.jc.test.collapsar.modal.User;

import java.util.List;

@Caches(targetType = User.class)
public interface UserCaches {
    @SetOperate
    void setById(@Key("id") Long id, @Value User user);

    @DelOperate
    void delById(@Key("id") Long id);

    @BatchDelOperate
    void batchDelById(@Keys("id") List<User> list);

//    @BatchGetOperate
//    void batchGetById(@Key("id") List<Long> idList);

//    @BatchSetOperate
//    void batchSetById(@Value List<User> user);

    @SetOperate
    void setByIdAndUserName(@Value User user);

    @GetOperate
    User getById(@Key("id") Long id);
}

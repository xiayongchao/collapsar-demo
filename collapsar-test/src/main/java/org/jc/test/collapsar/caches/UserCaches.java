package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.*;
import org.jc.test.collapsar.modal.User;

@Caches(targetType = User.class)
public interface UserCaches {
    @SetOperate
    void setById(@Key("id") Long id, @Value User user);

    @GetOperate
    User getById(@Key("id") Long id);

    @DelOperate
    void delById(@Key("id") Long id);

    @SetOperate
    void setByIdAndUserName(@Key("id") Long id, @Key("userName") String userName, @Value User user);
}

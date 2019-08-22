package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.annotation.Key;
import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.annotation.Value;
import org.jc.test.collapsar.modal.User;

@Caches(targetType = User.class)
public interface UserCaches {
    @SetOperate
    void setById(@Key("id") Long id, @Value User user);

    @SetOperate
    void setByIdAndUserName(@Key("id") Long id, @Key("userName") String userName, @Value User user);
}

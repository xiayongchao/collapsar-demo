package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.*;
import org.jc.test.collapsar.modal.User;

/**
 * @author xiayc
 * @date 2019/8/23
 */
@MultiCaches
public interface CommonCaches {
    @SetOperate
    @TargetModule(targetType = User.class)
    void setById(@Key("id") Long id, @Value User user);
}

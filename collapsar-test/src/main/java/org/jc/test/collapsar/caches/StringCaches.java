package org.jc.test.collapsar.caches;

import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.annotation.Key;
import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.annotation.Value;

@Caches(moduleName = "text", targetType = String.class)
public interface StringCaches {
    @SetOperate
    void setById(@Key("id") Long id, @Value String s);


//    @BatchSetOperate
//    void batchSetById(@Value List<String> stringList);
}

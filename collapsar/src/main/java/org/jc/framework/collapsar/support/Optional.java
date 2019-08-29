package org.jc.framework.collapsar.support;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xiayc
 * @date 2019/8/29
 */
public class Optional<T> {
    private List<T> list;
    private List<Integer> noHitIndexList;

    public List<T> get() {
        return list;
    }

    public List<Integer> getNoHitIndexList() {
        return noHitIndexList;
    }

    public boolean isAllHint() {
        return CollectionUtils.isEmpty(noHitIndexList);
    }
}

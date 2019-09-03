package org.jc.framework.collapsar.core.parser;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xiayc
 * @date 2019/8/29
 */
public class Optional<T> {
    private List<T> list;
    private List<Integer> noHitIndexList;

    public Optional() {
    }

    public Optional(List<T> list) {
        this.list = list;
    }

    public Optional(List<T> list, List<Integer> noHitIndexList) {
        this.list = list;
        this.noHitIndexList = noHitIndexList;
    }

    public static <T> Optional<T> of(List<T> list) {
        return new Optional<T>(list);
    }

    public static <T> Optional<T> of(List<T> list, List<Integer> noHitIndexList) {
        return new Optional<T>(list, noHitIndexList);
    }

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

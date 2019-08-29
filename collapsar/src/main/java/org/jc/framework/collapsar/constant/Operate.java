package org.jc.framework.collapsar.constant;

import org.springframework.util.StringUtils;

import java.util.StringJoiner;

/**
 * @author xiayc
 * @date 2019/3/27
 */
public enum Operate {
    NONE(null, null, false),
    SET("setBy", "@SetOperate"), GET("getBy", "@GetOperate"), DEL("delBy", "@DelOperate"),
    BATCH_SET("batchSetBy", "@BatchSetOperate"), BATCH_GET("batchGetBy", "@BatchGetOperate"),
    BATCH_DEL("batchDelBy", "@BatchDelOperate");
    private String prefix;
    private String name;
    private boolean active;

    Operate(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
        this.active = true;
    }

    Operate(String prefix, String name, boolean active) {
        this.prefix = prefix;
        this.name = name;
        this.active = active;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public boolean isStartOf(String string) {
        return StringUtils.hasLength(string) && string.startsWith(this.prefix);
    }

    public String removePrefix(String string) {
        if (isStartOf(string)) {
            string = string.substring(this.prefix.length(), string.length());
        }
        return string;
    }

    public final static String ENABLE_METHOD_ANNOTATIONS_STRING = getEnableMethodAnnotationsString();

    private static String getEnableMethodAnnotationsString() {
        StringJoiner stringJoiner = new StringJoiner("/");
        for (Operate operate : Operate.values()) {
            if (!operate.active) {
                continue;
            }
            stringJoiner.add(operate.getName());
        }
        return stringJoiner.toString();
    }
}

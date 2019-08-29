package org.jc.framework.collapsar.constant;

import org.jc.framework.collapsar.util.Strings;
import org.springframework.util.StringUtils;

import java.util.StringJoiner;

/**
 * @author xiayc
 * @date 2019/3/27
 */
public enum Operate {
    NONE(null, null, null, false),
    SET("setBy", "set*By", "@SetOperate"),
    GET("getBy", "get*By", "@GetOperate"),
    DEL("delBy", "del*By", "@DelOperate"),
    BATCH_SET("batchSetBy", "batchSet*By", "@BatchSetOperate"),
    BATCH_GET("batchGetBy", "batchGet*By", "@BatchGetOperate"),
    BATCH_DEL("batchDelBy", "batchDel*By", "@BatchDelOperate");

    private String prefix;
    private String multiPrefix;
    private String name;
    private boolean active;

    Operate(String prefix, String multiPrefix, String name) {
        this.prefix = prefix;
        this.multiPrefix = multiPrefix;
        this.name = name;
        this.active = true;
    }

    Operate(String prefix, String multiPrefix, String name, boolean active) {
        this.prefix = prefix;
        this.multiPrefix = multiPrefix;
        this.name = name;
        this.active = active;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMultiPrefix() {
        return multiPrefix;
    }

    public String getName() {
        return name;
    }

    public boolean validatePrefix(String string, boolean isMulti) {
        if (!StringUtils.hasLength(string) || string.length() < prefix.length()) {
            return false;
        }
        if (isMulti) {
            String[] strings = multiPrefix.split("\\*", 2);
            String left = strings[0];
            char[] rightChars = strings[1].toCharArray();
            if (!string.startsWith(left) || string.startsWith(prefix)) {
                return false;
            }
            char[] chars = string.toCharArray();
            int length = chars.length;
            int rightLength = rightChars.length;
            outer:
            for (int i = left.length(); i < length; i++) {
                if (i + rightLength > length) {
                    break;
                }
                for (int j = 0; j < rightLength; j++) {
                    if (chars[i + j] != rightChars[j]) {
                        continue outer;
                    }
                }
                return true;
            }
            return false;
        }
        return string.startsWith(this.prefix);
    }

    public String getModuleName(String string) {
        String[] strings = multiPrefix.split("\\*", 2);
        String left = strings[0];
        string = string.substring(left.length());
        char[] rightChars = strings[1].toCharArray();
        char[] chars = string.toCharArray();
        int length = chars.length;
        int rightLength = rightChars.length;
        outer:
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < rightLength; j++) {
                if (chars[i + j] != rightChars[j]) {
                    continue outer;
                }
            }
            return Strings.standingInitialLowercase(string.substring(0, i));
        }
        return null;
    }

    public String removePrefix(String string, String moduleName, boolean isMulti) {
        if (isMulti) {
            return string.substring(multiPrefix.length() - 1 + moduleName.length());
        }
        return string.substring(prefix.length());
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

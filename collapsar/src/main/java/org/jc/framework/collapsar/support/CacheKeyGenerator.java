package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.definition.ParamDefinition;

import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/21 23:15
 */
public class CacheKeyGenerator {
    private final String projectName;
    private final String moduleName;
    private final String connector;
    private final String cacheKeyPrefix;

    public CacheKeyGenerator(String projectName, String moduleName, String connector) {
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.connector = connector;
        this.cacheKeyPrefix = projectName + connector + moduleName;
    }

    public String buildCacheKey(Object[] args, ParamDefinition[] paramDefinitions) {
        int i = 0;
        StringJoiner nameJoiner = new StringJoiner("&");
        StringJoiner valueJoiner = new StringJoiner("&");
        for (ParamDefinition paramDefinition : paramDefinitions) {
            if (paramDefinition.isValue()) {
                continue;
            }
            nameJoiner.add(paramDefinition.getName());
            valueJoiner.add(String.valueOf(args[i]));
            i++;
        }
        return cacheKeyPrefix + connector + nameJoiner.toString() + "_" + valueJoiner.toString();
    }
}

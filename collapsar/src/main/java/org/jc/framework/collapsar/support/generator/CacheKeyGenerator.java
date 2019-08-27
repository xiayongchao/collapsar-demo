package org.jc.framework.collapsar.support.generator;

import org.jc.framework.collapsar.definition.ParamDefinition;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;

import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/21 23:15
 */
public class CacheKeyGenerator {
    private final String cacheKeyTemplate;
    private ParameterKeyBuilder[] parameterKeyBuilders;

    public CacheKeyGenerator(String projectName, String moduleName, String connector) {
        this.cacheKeyTemplate = projectName + connector + moduleName + connector + "%s_%s";
    }

    public String generate(Object[] args, ParamDefinition[] paramDefinitions) {
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
        return String.format(cacheKeyTemplate, nameJoiner.toString(), valueJoiner.toString());
    }
}

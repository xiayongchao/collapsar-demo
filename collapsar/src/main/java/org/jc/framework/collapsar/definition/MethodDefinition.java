package org.jc.framework.collapsar.definition;

/**
 * @author jc
 * @date 2019/8/28 0:32
 */
public class MethodDefinition {
    private final String projectName;
    private final String moduleName;
    private final String connector;
    private final Class<?> targetType;

    public MethodDefinition(CachesBeanDefinition cachesBeanDefinition) {
        this(cachesBeanDefinition.getProjectName(), cachesBeanDefinition.getModuleName(),
                cachesBeanDefinition.getConnector(), cachesBeanDefinition.getTargetType());
    }

    public MethodDefinition(String projectName, String moduleName, String connector, Class<?> targetType) {
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.connector = connector;
        this.targetType = targetType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getConnector() {
        return connector;
    }

    public Class<?> getTargetType() {
        return targetType;
    }
}

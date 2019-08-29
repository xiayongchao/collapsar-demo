package org.jc.framework.collapsar.definition;

import java.util.Objects;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class CachesBeanDefinition {
    private final String projectName;
    private final String connector;
    private final String beanName;
    private final String beanClassName;
    private final String moduleName;
    private final Class<?> targetType;

    public CachesBeanDefinition(String projectName, String connector, String beanName, String beanClassName, String moduleName, Class<?> targetType) {
        this.projectName = projectName;
        this.connector = connector;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
        this.moduleName = moduleName;
        this.targetType = targetType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getConnector() {
        return connector;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CachesBeanDefinition that = (CachesBeanDefinition) o;
        return Objects.equals(projectName, that.projectName) &&
                Objects.equals(connector, that.connector) &&
                Objects.equals(beanName, that.beanName) &&
                Objects.equals(beanClassName, that.beanClassName) &&
                Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(targetType, that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectName, connector, beanName, beanClassName, moduleName, targetType);
    }
}

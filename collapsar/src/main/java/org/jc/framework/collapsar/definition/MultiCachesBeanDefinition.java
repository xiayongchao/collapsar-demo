package org.jc.framework.collapsar.definition;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class MultiCachesBeanDefinition {
    private final String projectName;
    private final String connector;
    private final String beanName;
    private final String beanClassName;

    public MultiCachesBeanDefinition(String projectName, String connector, String beanName, String beanClassName) {
        this.projectName = projectName;
        this.connector = connector;
        this.beanName = beanName;
        this.beanClassName = beanClassName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MultiCachesBeanDefinition that = (MultiCachesBeanDefinition) o;

        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) {
            return false;
        }
        if (connector != null ? !connector.equals(that.connector) : that.connector != null) {
            return false;
        }
        if (beanName != null ? !beanName.equals(that.beanName) : that.beanName != null) {
            return false;
        }
        return beanClassName != null ? beanClassName.equals(that.beanClassName) : that.beanClassName == null;
    }

    @Override
    public int hashCode() {
        int result = projectName != null ? projectName.hashCode() : 0;
        result = 31 * result + (connector != null ? connector.hashCode() : 0);
        result = 31 * result + (beanName != null ? beanName.hashCode() : 0);
        result = 31 * result + (beanClassName != null ? beanClassName.hashCode() : 0);
        return result;
    }
}

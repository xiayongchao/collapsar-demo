package org.jc.framework.collapsar.definition;

import java.util.Arrays;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class PenetrationsBeanDefinition {
    private final String beanName;
    private final String beanClassName;
    private final String[] interfaceNames;

    public PenetrationsBeanDefinition(String beanName, String beanClassName, String[] interfaceNames) {
        this.beanName = beanName;
        this.beanClassName = beanClassName;
        this.interfaceNames = interfaceNames;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String[] getInterfaceNames() {
        return interfaceNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PenetrationsBeanDefinition that = (PenetrationsBeanDefinition) o;

        if (beanName != null ? !beanName.equals(that.beanName) : that.beanName != null) {
            return false;
        }
        if (beanClassName != null ? !beanClassName.equals(that.beanClassName) : that.beanClassName != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(interfaceNames, that.interfaceNames);
    }

    @Override
    public int hashCode() {
        int result = beanName != null ? beanName.hashCode() : 0;
        result = 31 * result + (beanClassName != null ? beanClassName.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(interfaceNames);
        return result;
    }
}

package org.jc.framework.collapsar.definition;

import org.jc.framework.collapsar.annotation.CollapsarComponentScan;
import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;

import java.util.Arrays;

/**
 * @author jc
 * @date 2019/8/21 0:26
 */
public class CollapsarComponentScanDefinition {
    /**
     * 项目名称/缓存一级结构名称
     *
     * @return
     */
    private final String projectName;

    /**
     * 扫描包名列表
     *
     * @return
     */
    private final String[] basePackages;

    /**
     * 匹配模式
     *
     * @return
     */
    private final String resourcePattern;

    /**
     * 缓存Key直接的连接符
     *
     * @return
     */
    private final String connector;

    public CollapsarComponentScanDefinition(EnableCollapsarConfiguration enableCollapsarConfiguration) {
        this.projectName = enableCollapsarConfiguration.projectName();
        this.basePackages = enableCollapsarConfiguration.basePackages();
        this.resourcePattern = enableCollapsarConfiguration.resourcePattern();
        this.connector = enableCollapsarConfiguration.connector();
    }

    public CollapsarComponentScanDefinition(CollapsarComponentScan collapsarComponentScan) {
        this.projectName = collapsarComponentScan.projectName();
        this.basePackages = collapsarComponentScan.basePackages();
        this.resourcePattern = collapsarComponentScan.resourcePattern();
        this.connector = collapsarComponentScan.connector();
    }

    public String getProjectName() {
        return projectName;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public String getResourcePattern() {
        return resourcePattern;
    }

    public String getConnector() {
        return connector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CollapsarComponentScanDefinition that = (CollapsarComponentScanDefinition) o;

        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(basePackages, that.basePackages)) {
            return false;
        }
        if (resourcePattern != null ? !resourcePattern.equals(that.resourcePattern) : that.resourcePattern != null) {
            return false;
        }
        return connector != null ? connector.equals(that.connector) : that.connector == null;
    }

    @Override
    public int hashCode() {
        int result = projectName != null ? projectName.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(basePackages);
        result = 31 * result + (resourcePattern != null ? resourcePattern.hashCode() : 0);
        result = 31 * result + (connector != null ? connector.hashCode() : 0);
        return result;
    }
}

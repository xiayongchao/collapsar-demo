package org.jc.framework.collapsar.definition;

import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;

/**
 * @author jc
 * @date 2019/8/21 0:26
 */
public class CollapsarConfigurationDefinition {
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

    public CollapsarConfigurationDefinition(EnableCollapsarConfiguration enableCollapsarConfiguration) {
        this.projectName = enableCollapsarConfiguration.projectName();
        this.basePackages = enableCollapsarConfiguration.basePackages();
        this.resourcePattern = enableCollapsarConfiguration.resourcePattern();
        this.connector = enableCollapsarConfiguration.connector();
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
}

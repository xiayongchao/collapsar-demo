package org.jc.framework.collapsar.core;

/**
 * @author jc
 * @date 2019/8/21 23:15
 */
public class CacheKeyGenerator {
    private String projectName;
    private String moduleName;
    private String connector;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }
}

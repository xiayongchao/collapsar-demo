package org.jc.framework.collapsar.proxy.invoker;

import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;

import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public abstract class AbstractMethodInvoker implements MethodInvoker {
    protected String methodFullName;
    protected String projectName;
    protected String moduleName;
    protected String connector;
    protected ParameterKeyBuilder[] parameterKeyBuilders;

    protected String generateKey(Object[] args) {
        StringJoiner keyNameJoiner = new StringJoiner("&");
        StringJoiner keyValueJoiner = new StringJoiner("&");
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            keyNameJoiner.add(parameterKeyBuilder.getName());
            keyValueJoiner.add(parameterKeyBuilder.buildKey(args));
        }
        return String.format("%s%s%s%s%s_%s", projectName, connector, moduleName,
                connector, keyNameJoiner.toString(), keyValueJoiner.toString());
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public void setMethodFullName(String methodFullName) {
        this.methodFullName = methodFullName;
    }

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

    public ParameterKeyBuilder[] getParameterKeyBuilders() {
        return parameterKeyBuilders;
    }

    public void setParameterKeyBuilders(ParameterKeyBuilder[] parameterKeyBuilders) {
        this.parameterKeyBuilders = parameterKeyBuilders;
    }
}

package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.Keys;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author jc
 * @date 2019/8/26 23:15
 */
public class KeysParameterParseHandler extends ParameterParseHandler {
    @Override
    public ParameterDefinition handleParameter(String methodFullName, Type parameterType, Annotation annotation) {
        if (!(annotation instanceof Keys)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleParameter(methodFullName, parameterType, annotation);
            }
            return getDefaultParameterDefinition(parameterType);
        }

        ParameterDefinition parameterDefinition = new ParameterDefinition();
        parameterDefinition.setType(parameterType);
        parameterDefinition.setParamType(ParamType.KEYS);
        parameterDefinition.setNames(((Keys) annotation).value());
        return parameterDefinition;
    }
}

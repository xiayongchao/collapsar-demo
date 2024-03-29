package org.jc.framework.collapsar.core.handler;

import org.jc.framework.collapsar.annotation.Key;
import org.jc.framework.collapsar.constant.ParamType;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author jc
 * @date 2019/8/26 23:15
 */
public class KeyParameterParseHandler extends ParameterParseHandler {
    @Override
    public ParameterDefinition handleParameter(String methodFullName, Type parameterType, Annotation annotation) {
        if (!(annotation instanceof Key)) {
            if (getNextHandler() != null) {
                return getNextHandler().handleParameter(methodFullName, parameterType, annotation);
            }
            return getDefaultParameterDefinition(parameterType);
        }

        ParameterDefinition parameterDefinition = new ParameterDefinition();
        parameterDefinition.setType(parameterType);
        parameterDefinition.setParamType(ParamType.KEY);

        String name;
        if (StringUtils.isEmpty(name = ((Key) annotation).value())) {
            throw new CollapsarException("方法[%s]的@Key形参value值不能为空", methodFullName);
        }
        parameterDefinition.setNames(new String[]{name});
        return parameterDefinition;
    }
}

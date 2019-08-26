package org.jc.framework.collapsar.support.parser;

import org.jc.framework.collapsar.annotation.SetOperate;
import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.ExpireCalculator;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jc
 * @date 2019/8/26 21:58
 */
public class SetMethodParser extends MethodParser {
    SetMethodParser(Method method, Class<?> targetType) {
        super(method, targetType);
    }

    @Override
    MethodParser parseMethodOperate() {
        if (!method.getName().startsWith(Operate.SET.getPrefix())) {
            throw new CollapsarException("[@SetOperate]注解方法[%s]请使用['%s']前缀",
                    methodFullName, Operate.SET.getPrefix());
        }
        methodDefinition.setOperate(Operate.SET);
        SetOperate setOperate = method.getDeclaredAnnotation(SetOperate.class);
        methodDefinition.setExpire(ExpireCalculator.calc(setOperate.expire(), setOperate.unit()));
        return this;
    }

    @Override
    MethodParser parseMethodParameter() {
        String nominateKey = method.getName().substring(Operate.SET.getPrefix().length());
        if (StringUtils.isEmpty(nominateKey)) {
            throw new CollapsarException("非法的[@SetOperate]方法[%s]命名,请提供命名规则", methodFullName);
        }
        ParameterDefinition[] parameterDefinitions = getParameterDefinitions();
        if (ArrayUtils.isEmpty(parameterDefinitions)) {
            throw new CollapsarException("[@SetOperate]方法[%s]入参数目不能为0", methodFullName);
        }
        String[] parameterKeys = nominateKey.split(METHOD_NAME_SEPARATOR);
        Map<String, Integer> parameterKey2IndexMap = new HashMap<>(parameterKeys.length);
        for (int i = 0; i < parameterKeys.length; i++) {
            parameterKey2IndexMap.put(Strings.standingInitialLowercase(parameterKeys[i]), i);
        }

        methodDefinition.setParamDefinitions(paramDefinitions);
        return this;
    }
}

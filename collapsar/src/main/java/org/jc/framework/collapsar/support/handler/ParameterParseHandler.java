package org.jc.framework.collapsar.support.handler;

import org.jc.framework.collapsar.annotation.Key;
import org.jc.framework.collapsar.annotation.Keys;
import org.jc.framework.collapsar.annotation.Value;
import org.jc.framework.collapsar.definition.ParameterDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jc
 * @date 2019/8/26 23:13
 */
public abstract class ParameterParseHandler {
    //下一个处理器
    private ParameterParseHandler nextHandler;

    //处理方法
    public abstract ParameterDefinition handleParameter(String methodFullName, Type parameterType, Annotation annotation);

    public ParameterParseHandler getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(ParameterParseHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    private static final ParameterParseHandler parameterParseHandler = init();

    private static ParameterParseHandler init() {
        ParameterParseHandler keyParameterParseHandler = new KeyParameterParseHandler();
        ParameterParseHandler keysParameterParseHandler = new KeysParameterParseHandler();
        ParameterParseHandler valueParameterParseHandler = new ValueParameterParseHandler();
        keysParameterParseHandler.setNextHandler(valueParameterParseHandler);
        keyParameterParseHandler.setNextHandler(keysParameterParseHandler);
        return keyParameterParseHandler;
    }

    public static ParameterDefinition praseHandleParameter(String methodFullName, Type parameterType, Annotation[] annotations) {
        List<Annotation> usefulAnnotations = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if (annotation == null) {
                    continue;
                }
                if (annotation instanceof Key) {
                    usefulAnnotations.add(annotation);
                } else if (annotation instanceof Keys) {
                    usefulAnnotations.add(annotation);
                } else if (annotation instanceof Value) {
                    usefulAnnotations.add(annotation);
                }
            }
        }
        if (CollectionUtils.isEmpty(usefulAnnotations)) {
            throw new CollapsarException("请在方法[%s]的形参上使用注解[@Key/@Keys/@Value]", methodFullName);
        }
        if (usefulAnnotations.size() > 1) {
            throw new CollapsarException("方法[%s]的同一形参上只能使用注解[@Key/@Keys/@Value]中的一个", methodFullName);
        }
        return parameterParseHandler.handleParameter(methodFullName, parameterType, usefulAnnotations.get(0));
    }
}

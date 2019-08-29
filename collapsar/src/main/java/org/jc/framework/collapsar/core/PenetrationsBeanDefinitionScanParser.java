package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.annotation.Penetrations;
import org.jc.framework.collapsar.definition.PenetrationsBeanDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

/**
 * ${@link Penetrations}Bean解析器
 *
 * @author xiayc
 * @date 2019/3/25
 */
public class PenetrationsBeanDefinitionScanParser extends CollapsarBeanDefinitionScanParser<PenetrationsBeanDefinition> {
    public PenetrationsBeanDefinitionScanParser() {
        super(Penetrations.class);
    }

    @Override
    protected PenetrationsBeanDefinition generateBeanDefinition(String projectName, String connector, AnnotationMetadata annotationMetadata, String annotationName) {
        return generateBeanDefinition(annotationMetadata, annotationName);
    }

    private PenetrationsBeanDefinition generateBeanDefinition(AnnotationMetadata annotationMetadata, String annotationName) {
        Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
        if (annotationTypes.isEmpty()) {
            return null;
        }
        String beanName, beanClassName = annotationMetadata.getClassName();
        String[] classNameArr, interfaceNames;
        for (String annotationType : annotationTypes) {
            if (!annotationType.equals(annotationName)) {
                continue;
            }
            //设置默认值
            classNameArr = annotationMetadata.getClassName().split("\\.");
            beanName = Strings.standingInitialLowercase(classNameArr[classNameArr.length - 1]);
            interfaceNames = annotationMetadata.getInterfaceNames();
            if (ArrayUtils.isEmpty(interfaceNames)) {
                throw new CollapsarException("无法解析Bean[%s],必须实现@Caches注解接口", beanClassName);
            }
            return new PenetrationsBeanDefinition(beanName, beanClassName, annotationMetadata.getInterfaceNames());
        }
        return null;
    }
}

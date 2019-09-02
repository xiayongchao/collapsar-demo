package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.annotation.MultiCaches;
import org.jc.framework.collapsar.definition.MultiCachesBeanDefinition;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * ${@link MultiCaches}解析器
 *
 * @author xiayc
 * @date 2019/3/25
 */
public class MultiCachesBeanDefinitionScanParser extends CollapsarBeanDefinitionScanParser<MultiCachesBeanDefinition> {
    public MultiCachesBeanDefinitionScanParser() {
        super(MultiCaches.class);
    }

    @Override
    protected MultiCachesBeanDefinition generateBeanDefinition(final String projectName, final String connector, final AnnotationMetadata annotationMetadata, final String annotationName) {
        Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
        if (annotationTypes.isEmpty()) {
            return null;
        }
        String beanName;
        Map<String, Object> map;
        String[] classNameArr;
        for (String annotationType : annotationTypes) {
            if (!annotationType.equals(annotationName)) {
                continue;
            }
            map = annotationMetadata.getAnnotationAttributes(annotationType, false);
            beanName = map.get("value").toString();
            if (!StringUtils.hasText(beanName)) {
                //设置默认值
                classNameArr = annotationMetadata.getClassName().split("\\.");
                beanName = Strings.standingInitialLowercase(classNameArr[classNameArr.length - 1]);
            }

            return new MultiCachesBeanDefinition(projectName, connector, beanName,
                    annotationMetadata.getClassName());
        }
        return null;
    }
}

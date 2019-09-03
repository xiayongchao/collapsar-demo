package org.jc.framework.collapsar.starter;


import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * ${@link Caches}解析器
 *
 * @author xiayc
 * @date 2019/3/25
 */
public class CachesBeanDefinitionScanParser extends CollapsarBeanDefinitionScanParser<CachesBeanDefinition> {
    public CachesBeanDefinitionScanParser() {
        super(Caches.class);
    }

    private boolean isSupportTargetType(Class targetType) {
        if (targetType.isInterface()) {
            return false;
        }
        if (targetType.isAnnotation()) {
            return false;
        }
        if (targetType.isEnum()) {
            return false;
        }
        if (targetType.isArray()) {
            return false;
        }
        try {
            targetType.asSubclass(Collection.class);
            return false;
        } catch (ClassCastException e) {
        }
        try {
            targetType.asSubclass(Map.class);
            return false;
        } catch (ClassCastException e) {
        }
        return true;
    }

    @Override
    protected CachesBeanDefinition generateBeanDefinition(final String projectName,
                                                          final String connector, final AnnotationMetadata annotationMetadata, final String annotationName) {
        Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
        if (annotationTypes.isEmpty()) {
            return null;
        }
        String beanName, moduleName;
        Class targetType;
        Map<String, Object> map;
        String[] classNameArr, names;
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
            //---------------
            targetType = (Class) map.get("targetType");
            if (targetType == null) {
                throw new CollapsarException("%s#target()不能为空", Caches.class.getName());
            }
            if (!isSupportTargetType(targetType)) {
                throw new CollapsarException("不支持的[%s#target()]类型[%s]", Caches.class.getName(), targetType.getName());
            }
            //---------------
            moduleName = map.get("moduleName").toString();
            if (!StringUtils.hasText(beanName) || moduleName.equals(Strings.NULL_STRING)) {
                //设置默认值，即类名
                moduleName = targetType.getName();
                if (StringUtils.hasText(moduleName) && moduleName.contains(".")) {
                    names = moduleName.split("\\.");
                    moduleName = Strings.standingInitialLowercase(names[names.length - 1]);
                }
            }

            return new CachesBeanDefinition(projectName, connector, beanName,
                    annotationMetadata.getClassName(), moduleName, targetType);
        }
        return null;
    }
}

package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.CollapsarConfigurationDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.jc.framework.collapsar.util.Strings;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SpringJca Bean解析器
 *
 * @author xiayc
 * @date 2019/3/25
 */
public class CollapsarBeanDefinitionScanParser implements ResourceLoaderAware, EnvironmentAware {
    private Environment environment;
    private MetadataReaderFactory metadataReaderFactory;
    private ResourcePatternResolver resourcePatternResolver;
    private final Set<String> scannedCachesBeans = new HashSet<>();

    /**
     * 扫描并解析SpringJca组件
     *
     * @param collapsarConfigurationDefinition
     * @return
     * @throws IOException
     */
    Set<CachesBeanDefinition> scanParse(final CollapsarConfigurationDefinition collapsarConfigurationDefinition) throws IOException {
        String[] basePackages;
        String resourcePattern;
        Set<CachesBeanDefinition> cachesBeanDefinitions = new HashSet<>();

        if (ArrayUtils.isEmpty(basePackages = collapsarConfigurationDefinition.getBasePackages()) || !StringUtils.hasText(resourcePattern = collapsarConfigurationDefinition.getResourcePattern())) {
            return cachesBeanDefinitions;
        }
        TypeFilter cachesFilter = new AnnotationTypeFilter(Caches.class);
        MetadataReader metadataReader;
        AnnotationMetadata annotationMetadata;
        String classPattern = "classpath*:%s/%s";
        Resource[] resources;
        CachesBeanDefinition cachesBeanDefinition;
        String annotationName = Caches.class.getName();
        String projectName = collapsarConfigurationDefinition.getProjectName();
        String connector = collapsarConfigurationDefinition.getConnector();
        for (String basePackage : basePackages) {
            if (!StringUtils.hasText(basePackage)) {
                continue;
            }
            resources = this.resourcePatternResolver.getResources(String.format(classPattern, ClassUtils.convertClassNameToResourcePath(
                    this.environment.resolveRequiredPlaceholders(basePackage)), resourcePattern));
            if (ArrayUtils.isEmpty(resources)) {
                continue;
            }
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                annotationMetadata = metadataReader.getAnnotationMetadata();
                if (cachesFilter.match(metadataReader, this.metadataReaderFactory)) {
                    if (scannedCachesBeans.contains(annotationMetadata.getClassName())) {
                        throw new CollapsarException("发现重复注册的@Caches Bean[%s]", annotationMetadata.getClassName());
                    } else if ((cachesBeanDefinition = generateCachesBeanDefinition(projectName, connector, annotationMetadata, annotationName)) != null) {
                        scannedCachesBeans.add(annotationMetadata.getClassName());
                        cachesBeanDefinitions.add(cachesBeanDefinition);
                    }
                }
            }
        }
        return cachesBeanDefinitions;
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

    private CachesBeanDefinition generateCachesBeanDefinition(final String projectName,
                                                              final String connector, final AnnotationMetadata annotationMetadata, final String annotationName) {
        Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
        if (annotationTypes.isEmpty()) {
            return null;
        }
        String beanName, moduleName;
        Class targetType;
        for (String annotationType : annotationTypes) {
            if (!annotationType.equals(annotationName)) {
                continue;
            }
            Map<String, Object> map = annotationMetadata.getAnnotationAttributes(annotationType, false);
            beanName = map.get("value").toString();
            if (!StringUtils.hasText(beanName)) {
                //设置默认值
                String[] classNameArr = annotationMetadata.getClassName().split("\\.");
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
                    String[] names = moduleName.split("\\.");
                    moduleName = names[names.length - 1];
                }
            }

            return new CachesBeanDefinition(projectName, connector, beanName,
                    annotationMetadata.getClassName(), moduleName, targetType);
        }

        return null;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
    }
}

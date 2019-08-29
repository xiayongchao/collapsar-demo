package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.annotation.MultiCaches;
import org.jc.framework.collapsar.definition.CollapsarComponentScanDefinition;
import org.jc.framework.collapsar.definition.MultiCachesBeanDefinition;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ${@link MultiCaches}解析器
 *
 * @author xiayc
 * @date 2019/3/25
 */
public class MultiCachesBeanDefinitionScanParser implements ResourceLoaderAware, EnvironmentAware {
    private Environment environment;
    private MetadataReaderFactory metadataReaderFactory;
    private ResourcePatternResolver resourcePatternResolver;

    /**
     * 扫描并解析${@link MultiCaches}组件
     *
     * @param collapsarComponentScanDefinition
     * @return
     * @throws IOException
     */
    Set<MultiCachesBeanDefinition> scanParse(final Set<String> collapsarBeans, final CollapsarComponentScanDefinition collapsarComponentScanDefinition) throws IOException {
        String[] basePackages;
        String resourcePattern;
        Set<MultiCachesBeanDefinition> multiCachesBeanDefinitions = new HashSet<>();

        if (ArrayUtils.isEmpty(basePackages = collapsarComponentScanDefinition.getBasePackages()) || !StringUtils.hasText(resourcePattern = collapsarComponentScanDefinition.getResourcePattern())) {
            return multiCachesBeanDefinitions;
        }
        TypeFilter cachesFilter = new AnnotationTypeFilter(MultiCaches.class);
        MetadataReader metadataReader;
        AnnotationMetadata annotationMetadata;
        String classPattern = "classpath*:%s/%s";
        Resource[] resources;
        MultiCachesBeanDefinition multiCachesBeanDefinition;
        String annotationName = MultiCaches.class.getName();
        String projectName = collapsarComponentScanDefinition.getProjectName();
        String connector = collapsarComponentScanDefinition.getConnector();
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
                    if (collapsarBeans.contains(annotationMetadata.getClassName())) {
                        throw new CollapsarException("发现重复注册的@MultiCaches Bean[%s]", annotationMetadata.getClassName());
                    } else if ((multiCachesBeanDefinition = generateMultiCachesBeanDefinition(projectName, connector, annotationMetadata, annotationName)) != null) {
                        collapsarBeans.add(annotationMetadata.getClassName());
                        multiCachesBeanDefinitions.add(multiCachesBeanDefinition);
                    }
                }
            }
        }
        return multiCachesBeanDefinitions;
    }

    private MultiCachesBeanDefinition generateMultiCachesBeanDefinition(final String projectName,
                                                                        final String connector, final AnnotationMetadata annotationMetadata, final String annotationName) {
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

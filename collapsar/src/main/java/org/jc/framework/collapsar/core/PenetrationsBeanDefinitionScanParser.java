package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.annotation.Penetrations;
import org.jc.framework.collapsar.definition.CollapsarComponentScanDefinition;
import org.jc.framework.collapsar.definition.PenetrationsBeanDefinition;
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
import java.util.Set;

/**
 * ${@link Penetrations}Bean解析器
 *
 * @author xiayc
 * @date 2019/3/25
 */
public class PenetrationsBeanDefinitionScanParser implements ResourceLoaderAware, EnvironmentAware {
    private Environment environment;
    private MetadataReaderFactory metadataReaderFactory;
    private ResourcePatternResolver resourcePatternResolver;

    /**
     * 扫描并解析${@link Penetrations}组件
     *
     * @param collapsarComponentScanDefinition
     * @return
     * @throws IOException
     */
    Set<PenetrationsBeanDefinition> scanParse(final Set<String> collapsarBeans, final CollapsarComponentScanDefinition collapsarComponentScanDefinition) throws IOException {
        String[] basePackages;
        String resourcePattern;
        Set<PenetrationsBeanDefinition> cachesBeanDefinitions = new HashSet<>();

        if (ArrayUtils.isEmpty(basePackages = collapsarComponentScanDefinition.getBasePackages()) || !StringUtils.hasText(resourcePattern = collapsarComponentScanDefinition.getResourcePattern())) {
            return cachesBeanDefinitions;
        }
        TypeFilter cachesFilter = new AnnotationTypeFilter(Penetrations.class);
        MetadataReader metadataReader;
        AnnotationMetadata annotationMetadata;
        String classPattern = "classpath*:%s/%s";
        Resource[] resources;
        PenetrationsBeanDefinition cachesBeanDefinition;
        String annotationName = Penetrations.class.getName();
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
                        throw new CollapsarException("发现重复注册的@Penetrations Bean[%s]", annotationMetadata.getClassName());
                    } else if ((cachesBeanDefinition = generatePenetrationsBeanDefinition(annotationMetadata, annotationName)) != null) {
                        collapsarBeans.add(annotationMetadata.getClassName());
                        cachesBeanDefinitions.add(cachesBeanDefinition);
                    }
                }
            }
        }
        return cachesBeanDefinitions;
    }

    private PenetrationsBeanDefinition generatePenetrationsBeanDefinition(final AnnotationMetadata annotationMetadata, final String annotationName) {
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

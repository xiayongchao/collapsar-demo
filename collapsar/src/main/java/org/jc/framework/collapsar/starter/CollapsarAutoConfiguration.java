package org.jc.framework.collapsar.starter;

import org.jc.framework.collapsar.annotation.EnableCollapsar;
import org.jc.framework.collapsar.core.CollapsarBeanMethodHandler;
import org.jc.framework.collapsar.core.CollapsarBeanMethodHandlerImpl;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author xiayc
 * @date 2019/3/25
 */
@Configuration
@ConditionalOnBean(annotation = EnableCollapsar.class)
public class CollapsarAutoConfiguration {
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.CachesBeanMethodHandler")
    public CollapsarBeanMethodHandler cachesBeanMethodHandler(CacheRepository cacheRepository) {
        return new CollapsarBeanMethodHandlerImpl(cacheRepository);
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.starter.CachesBeanDefinitionScanParser")
    public CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser() {
        return new CachesBeanDefinitionScanParser();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.starter.MultiCachesBeanDefinitionScanParser")
    public MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser() {
        return new MultiCachesBeanDefinitionScanParser();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.starter.CollapsarMergedBeanDefinitionPostProcessor")
    public CollapsarMergedBeanDefinitionPostProcessor collapsarMergedBeanDefinitionPostProcessor(
            CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser, MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser,
            CollapsarBeanMethodHandler collapsarBeanMethodHandler) {
        return new CollapsarMergedBeanDefinitionPostProcessor(cachesBeanDefinitionScanParser, multiCachesBeanDefinitionScanParser,
                collapsarBeanMethodHandler);
    }
}

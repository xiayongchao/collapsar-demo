package org.jc.framework.collapsar.core;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author xiayc
 * @date 2019/3/25
 */
@Configuration
public class CollapsarConfiguration {
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.CachesBeanMethodHandlerImpl")
    public CachesBeanMethodHandler cachesBeanMethodHandler() {
        return new CachesBeanMethodHandlerImpl();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.CollapsarBeanDefinitionScanParser")
    public CollapsarBeanDefinitionScanParser collapsarBeanDefinitionScanParser() {
        return new CollapsarBeanDefinitionScanParser();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.CollapsarMergedBeanDefinitionPostProcessor")
    public CollapsarMergedBeanDefinitionPostProcessor collapsarMergedBeanDefinitionPostProcessor(
            CollapsarBeanDefinitionScanParser collapsarBeanDefinitionScanParser, CachesBeanMethodHandler cachesBeanMethodHandler) {
        return new CollapsarMergedBeanDefinitionPostProcessor(collapsarBeanDefinitionScanParser, cachesBeanMethodHandler);
    }
}

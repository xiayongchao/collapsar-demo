package org.jc.framework.collapsar.core;

import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.proxy.CollapsarBeanMethodHandler;
import org.jc.framework.collapsar.proxy.CollapsarBeanMethodHandlerImpl;
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
    @Bean(name = "org.jc.framework.collapsar.proxy.CachesBeanMethodHandler")
    public CollapsarBeanMethodHandler cachesBeanMethodHandler(CacheRepository cacheRepository) {
        return new CollapsarBeanMethodHandlerImpl(cacheRepository);
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.CachesBeanDefinitionScanParser")
    public CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser() {
        return new CachesBeanDefinitionScanParser();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.MultiCachesBeanDefinitionScanParser")
    public MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser() {
        return new MultiCachesBeanDefinitionScanParser();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.PenetrationsBeanDefinitionScanParser")
    public PenetrationsBeanDefinitionScanParser penetrationsBeanDefinitionScanParser() {
        return new PenetrationsBeanDefinitionScanParser();
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "org.jc.framework.collapsar.core.CollapsarMergedBeanDefinitionPostProcessor")
    public CollapsarMergedBeanDefinitionPostProcessor collapsarMergedBeanDefinitionPostProcessor(
            CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser, MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser,
            PenetrationsBeanDefinitionScanParser penetrationsBeanDefinitionScanParser,
            CollapsarBeanMethodHandler collapsarBeanMethodHandler) {
        return new CollapsarMergedBeanDefinitionPostProcessor(cachesBeanDefinitionScanParser, multiCachesBeanDefinitionScanParser,
                penetrationsBeanDefinitionScanParser, collapsarBeanMethodHandler);
    }
}

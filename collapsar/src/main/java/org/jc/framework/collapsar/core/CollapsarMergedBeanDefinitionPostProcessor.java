package org.jc.framework.collapsar.core;


import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.jc.framework.collapsar.annotation.*;
import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.CollapsarConfigurationDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author xiayc
 * @date 2019/3/25
 */
public class CollapsarMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;
    private final CollapsarBeanDefinitionScanParser collapsarBeanDefinitionScanParser;
    private final CachesBeanMethodHandler cachesBeanMethodHandler;


    CollapsarMergedBeanDefinitionPostProcessor(CollapsarBeanDefinitionScanParser collapsarBeanDefinitionScanParser,
                                               CachesBeanMethodHandler cachesBeanMethodHandler) {
        this.collapsarBeanDefinitionScanParser = collapsarBeanDefinitionScanParser;
        this.cachesBeanMethodHandler = cachesBeanMethodHandler;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        EnableCollapsarConfiguration enableCollapsarConfiguration = AnnotationUtils.findAnnotation(beanType, EnableCollapsarConfiguration.class);
        if (enableCollapsarConfiguration == null || ArrayUtils.isEmpty(enableCollapsarConfiguration.basePackages())) {
            return;
        }
        try {
            final Set<CachesBeanDefinition> cachesBeanDefinitions = collapsarBeanDefinitionScanParser.
                    scanParse(new CollapsarConfigurationDefinition(enableCollapsarConfiguration));
            this.generateInterfaceProxy(cachesBeanDefinitions);
        } catch (IOException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            throw new CollapsarException(e, "解析CachesBeanDefinition异常");
        }
    }

    /**
     * 生成接口代理
     *
     * @param cachesBeanDefinitions
     */
    private void generateInterfaceProxy(Set<CachesBeanDefinition> cachesBeanDefinitions) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (CollectionUtils.isEmpty(cachesBeanDefinitions)) {
            return;
        }
        String beanName, beanClassName;
        Class beanType;
        ProxyFactory factory;
        Object object;
        for (CachesBeanDefinition cachesBeanDefinition : cachesBeanDefinitions) {
            beanName = cachesBeanDefinition.getBeanName();
            beanClassName = cachesBeanDefinition.getBeanClassName();
            beanType = Class.forName(beanClassName, true, this.beanFactory.getBeanClassLoader());
            if (!beanType.isInterface()) {
                throw new CollapsarException("请在interface类型上使用注解[%s]", Caches.class.getName());
            }
            if (this.beanFactory.containsBean(beanName)) {
                continue;
            }
            List<Method> methods = Arrays.asList(beanType.getDeclaredMethods());
            factory = new ProxyFactory();
            factory.setInterfaces(new Class[]{beanType});
            factory.setFilter((m) -> {
                return methods.contains(m) && (m.isAnnotationPresent(SetOperate.class)
                        || m.isAnnotationPresent(GetOperate.class) || m.isAnnotationPresent(DelOperate.class));
            });

            object = factory.create(new Class[0], new Object[0]);
            ((Proxy) object).setHandler(cachesBeanMethodHandler);

            methods.forEach(method -> {
                cachesBeanMethodHandler.registerMethod(method, cachesBeanDefinition);
            });

            //将生成的对象注册到Spring容器
            this.beanFactory.registerSingleton(beanName, object);
        }
    }
}

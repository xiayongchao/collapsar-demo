package org.jc.framework.collapsar.core;


import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.annotation.CollapsarComponentScan;
import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;
import org.jc.framework.collapsar.annotation.MultiCaches;
import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.CollapsarComponentScanDefinition;
import org.jc.framework.collapsar.definition.MultiCachesBeanDefinition;
import org.jc.framework.collapsar.definition.PenetrationsBeanDefinition;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.CachesBeanMethodHandler;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xiayc
 * @date 2019/3/25
 */
public class CollapsarMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;
    private final CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser;
    private final MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser;
    private final PenetrationsBeanDefinitionScanParser penetrationsBeanDefinitionScanParser;
    private final CachesBeanMethodHandler cachesBeanMethodHandler;
    private final Set<String> collapsarBeans = new HashSet<>();

    CollapsarMergedBeanDefinitionPostProcessor(CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser,
                                               MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser,
                                               PenetrationsBeanDefinitionScanParser penetrationsBeanDefinitionScanParser,
                                               CachesBeanMethodHandler cachesBeanMethodHandler) {
        this.cachesBeanDefinitionScanParser = cachesBeanDefinitionScanParser;
        this.multiCachesBeanDefinitionScanParser = multiCachesBeanDefinitionScanParser;
        this.penetrationsBeanDefinitionScanParser = penetrationsBeanDefinitionScanParser;
        this.cachesBeanMethodHandler = cachesBeanMethodHandler;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        EnableCollapsarConfiguration enableCollapsarConfiguration = AnnotationUtils.findAnnotation(beanType, EnableCollapsarConfiguration.class);
        Set<CollapsarComponentScanDefinition> collapsarComponentScanDefinitions = new HashSet<>();
        if (enableCollapsarConfiguration != null
                && ArrayUtils.isNotEmpty(enableCollapsarConfiguration.basePackages())) {
            collapsarComponentScanDefinitions.add(new CollapsarComponentScanDefinition(enableCollapsarConfiguration));
        }
        CollapsarComponentScan collapsarComponentScan = AnnotationUtils.findAnnotation(beanType, CollapsarComponentScan.class);
        if (collapsarComponentScan != null
                && ArrayUtils.isNotEmpty(collapsarComponentScan.basePackages())) {
            collapsarComponentScanDefinitions.add(new CollapsarComponentScanDefinition(collapsarComponentScan));
        }
        if (CollectionUtils.isEmpty(collapsarComponentScanDefinitions)) {
            return;
        }
        for (CollapsarComponentScanDefinition collapsarComponentScanDefinition : collapsarComponentScanDefinitions) {
            try {
                final Set<CachesBeanDefinition> cachesBeanDefinitions = cachesBeanDefinitionScanParser.scanParse(collapsarBeans, collapsarComponentScanDefinition);
                final Set<MultiCachesBeanDefinition> multiCachesBeanDefinitions = multiCachesBeanDefinitionScanParser.scanParse(collapsarBeans, collapsarComponentScanDefinition);
                final Set<PenetrationsBeanDefinition> penetrationsBeanDefinitions = penetrationsBeanDefinitionScanParser.scanParse(collapsarBeans, collapsarComponentScanDefinition);
                final Map<String, Object> penetrationsBeanMap = createPenetrationsBeans(penetrationsBeanDefinitions);
                this.registerCollapsarBeans(cachesBeanDefinitions, multiCachesBeanDefinitions, penetrationsBeanMap);
            } catch (IOException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                throw new CollapsarException(e, "解析CachesBeanDefinition异常");
            }
        }
    }

    /**
     * 注册Bean
     *
     * @param cachesBeanDefinitions
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private void registerCollapsarBeans(final Set<CachesBeanDefinition> cachesBeanDefinitions,
                                        final Set<MultiCachesBeanDefinition> multiCachesBeanDefinitions,
                                        final Map<String, Object> penetrationsBeanMap) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (!CollectionUtils.isEmpty(cachesBeanDefinitions)) {
            for (CachesBeanDefinition cachesBeanDefinition : cachesBeanDefinitions) {
                registerCollapsarBean(cachesBeanDefinition, penetrationsBeanMap);
            }
        }
        if (!CollectionUtils.isEmpty(multiCachesBeanDefinitions)) {
            for (MultiCachesBeanDefinition multiCachesBeanDefinition : multiCachesBeanDefinitions) {
                registerCollapsarBean(multiCachesBeanDefinition, penetrationsBeanMap);
            }
        }
    }

    private void registerCollapsarBean(final MultiCachesBeanDefinition multiCachesBeanDefinition,
                                       final Map<String, Object> penetrationsBeanMap) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        ProxyFactory factory;
        Object object;
        String beanName = multiCachesBeanDefinition.getBeanName();
        String beanClassName = multiCachesBeanDefinition.getBeanClassName();
        Class beanType = Class.forName(beanClassName, true, this.beanFactory.getBeanClassLoader());
        if (!beanType.isInterface()) {
            throw new CollapsarException("请在interface类型上使用注解[%s]", MultiCaches.class.getName());
        }
        if (this.beanFactory.containsBean(beanName)) {
            throw new CollapsarException("注册@MultiCaches Bean[%s]失败,已经存在同名Bean[%s]", beanClassName, beanName);
        }
        List<Method> methods = Arrays.asList(beanType.getDeclaredMethods());
        factory = new ProxyFactory();
        factory.setInterfaces(new Class[]{beanType});

        object = factory.create(new Class[0], new Object[0]);
        ((Proxy) object).setHandler(cachesBeanMethodHandler);

        for (Method method : methods) {
            cachesBeanMethodHandler.registerMethod(method, penetrationsBeanMap.get(beanClassName), multiCachesBeanDefinition);
        }

        //将生成的对象注册到Spring容器
        this.beanFactory.registerSingleton(beanName, object);
    }

    private void registerCollapsarBean(final CachesBeanDefinition cachesBeanDefinition,
                                       final Map<String, Object> penetrationsBeanMap) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        ProxyFactory factory;
        Object object;
        String beanName = cachesBeanDefinition.getBeanName();
        String beanClassName = cachesBeanDefinition.getBeanClassName();
        Class beanType = Class.forName(beanClassName, true, this.beanFactory.getBeanClassLoader());
        if (!beanType.isInterface()) {
            throw new CollapsarException("请在interface类型上使用注解[%s]", Caches.class.getName());
        }
        if (this.beanFactory.containsBean(beanName)) {
            throw new CollapsarException("注册@Caches Bean[%s]失败,已经存在同名Bean[%s]", beanClassName, beanName);
        }
        List<Method> methods = Arrays.asList(beanType.getDeclaredMethods());
        factory = new ProxyFactory();
        factory.setInterfaces(new Class[]{beanType});

        object = factory.create(new Class[0], new Object[0]);
        ((Proxy) object).setHandler(cachesBeanMethodHandler);

        for (Method method : methods) {
            cachesBeanMethodHandler.registerMethod(method, penetrationsBeanMap.get(beanClassName), cachesBeanDefinition);
        }

        //将生成的对象注册到Spring容器
        this.beanFactory.registerSingleton(beanName, object);
    }

    private Map<String, Object> createPenetrationsBeans(final Set<PenetrationsBeanDefinition> penetrationsBeanDefinitions) throws ClassNotFoundException {
        final Map<String, Object> penetrationsBeanMap = new HashMap<>();
        if (CollectionUtils.isEmpty(penetrationsBeanDefinitions)) {
            return penetrationsBeanMap;
        }
        String[] cachesInterfaceNames;
        String penetrationsBeanClassName;
        Class<?> penetrationsBeanType;
        Object penetrationsBean;
        Class<?> cachesInterfaceType;
        for (PenetrationsBeanDefinition penetrationsBeanDefinition : penetrationsBeanDefinitions) {
            penetrationsBeanClassName = penetrationsBeanDefinition.getBeanClassName();
            if (ArrayUtils.isEmpty(cachesInterfaceNames = penetrationsBeanDefinition.getInterfaceNames())) {
                throw new CollapsarException("无法解析Bean[%s],必须实现@Caches注解接口", penetrationsBeanClassName);
            }
            penetrationsBeanType = Class.forName(penetrationsBeanClassName, true, this.beanFactory.getBeanClassLoader());
            penetrationsBean = beanFactory.createBean(penetrationsBeanType, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
            for (String cachesInterfaceName : cachesInterfaceNames) {
                cachesInterfaceType = Class.forName(cachesInterfaceName, true, this.beanFactory.getBeanClassLoader());
                if (!cachesInterfaceType.isAnnotationPresent(Caches.class)) {
                    continue;
                }
                if (penetrationsBeanMap.containsKey(cachesInterfaceName)) {
                    throw new CollapsarException("无法解析Bean[%s],@Caches注解接口[%s]已经存在@Penetrations实例", penetrationsBeanClassName, cachesInterfaceName);
                }
                penetrationsBeanMap.put(cachesInterfaceName, penetrationsBean);
            }
        }
        return penetrationsBeanMap;
    }
}

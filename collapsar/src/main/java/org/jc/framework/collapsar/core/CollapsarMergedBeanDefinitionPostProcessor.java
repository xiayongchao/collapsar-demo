package org.jc.framework.collapsar.core;


import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.jc.framework.collapsar.annotation.Caches;
import org.jc.framework.collapsar.annotation.CollapsarComponentScan;
import org.jc.framework.collapsar.annotation.EnableCollapsarConfiguration;
import org.jc.framework.collapsar.annotation.MultiCaches;
import org.jc.framework.collapsar.definition.*;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.proxy.CollapsarBeanMethodHandler;
import org.jc.framework.collapsar.util.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author xiayc
 * @date 2019/3/25
 */
public class CollapsarMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware, ApplicationListener<ContextRefreshedEvent> {
    private DefaultListableBeanFactory beanFactory;
    private final CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser;
    private final MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser;
    private final PenetrationsBeanDefinitionScanParser penetrationsBeanDefinitionScanParser;
    private final CollapsarBeanMethodHandler collapsarBeanMethodHandler;
    private final Set<String> collapsarBeans = new HashSet<>();
    private final Set<Object> autowireBeans = new HashSet<>();

    CollapsarMergedBeanDefinitionPostProcessor(CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser,
                                               MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser,
                                               PenetrationsBeanDefinitionScanParser penetrationsBeanDefinitionScanParser,
                                               CollapsarBeanMethodHandler collapsarBeanMethodHandler) {
        this.cachesBeanDefinitionScanParser = cachesBeanDefinitionScanParser;
        this.multiCachesBeanDefinitionScanParser = multiCachesBeanDefinitionScanParser;
        this.penetrationsBeanDefinitionScanParser = penetrationsBeanDefinitionScanParser;
        this.collapsarBeanMethodHandler = collapsarBeanMethodHandler;
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
                Set<CachesBeanDefinition> cachesBeanDefinitions = cachesBeanDefinitionScanParser.scanParse(collapsarBeans, collapsarComponentScanDefinition);
                Set<MultiCachesBeanDefinition> multiCachesBeanDefinitions = multiCachesBeanDefinitionScanParser.scanParse(collapsarBeans, collapsarComponentScanDefinition);
                /*Set<PenetrationsBeanDefinition> penetrationsBeanDefinitions = penetrationsBeanDefinitionScanParser.scanParse(collapsarBeans, collapsarComponentScanDefinition);
                Map<String, Object> penetrationsBeanMap = createPenetrationsBeans(penetrationsBeanDefinitions);*/
                Map<String, Object> penetrationsBeanMap = new HashMap<>();
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
                registerCollapsarBean(cachesBeanDefinition.getBeanName(), cachesBeanDefinition.getBeanClassName(),
                        Caches.class, new MethodDefinition(cachesBeanDefinition), penetrationsBeanMap);
            }
        }
        if (!CollectionUtils.isEmpty(multiCachesBeanDefinitions)) {
            for (MultiCachesBeanDefinition multiCachesBeanDefinition : multiCachesBeanDefinitions) {
                registerCollapsarBean(multiCachesBeanDefinition.getBeanName(), multiCachesBeanDefinition.getBeanClassName(),
                        MultiCaches.class, new MethodDefinition(multiCachesBeanDefinition), penetrationsBeanMap);
            }
        }
    }

    private void registerCollapsarBean(String beanName, String beanClassName, Class<? extends Annotation> annotationClass, MethodDefinition methodDefinition,
                                       Map<String, Object> penetrationsBeanMap) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        ProxyFactory factory;
        Object object;
        Class beanType = Class.forName(beanClassName, true, this.beanFactory.getBeanClassLoader());
        if (beanType.isEnum() || beanType.isArray() || beanType.isAnnotation()) {
            throw new CollapsarException("[%s]类型上不支持使用注解[@%s]", beanClassName, annotationClass.getName());
        }
        if (this.beanFactory.containsBean(beanName)) {
            throw new CollapsarException("注册@%s Bean[%s]失败,已经存在同名Bean[name=%s]", annotationClass.getName(), beanClassName, beanName);
        }
        Method[] methods = beanType.getDeclaredMethods();
        factory = new ProxyFactory();

        if (beanType.isInterface()) {
            factory.setInterfaces(new Class[]{beanType});
        } else {
            factory.setSuperclass(beanType);
        }

        object = factory.create(new Class[0], new Object[0]);
        ((Proxy) object).setHandler(collapsarBeanMethodHandler);

        for (Method method : methods) {
            collapsarBeanMethodHandler.registerMethod(method, penetrationsBeanMap.get(beanClassName), methodDefinition);
        }

        autowireBeans.add(object);
        //将生成的对象注册到Spring容器
        this.beanFactory.registerSingleton(beanName, object);
    }

    private Map<String, Object> createPenetrationsBeans(Set<PenetrationsBeanDefinition> penetrationsBeanDefinitions) throws ClassNotFoundException {
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
                throw new CollapsarException("无法解析Bean[%s],必须实现使用了@Caches/@MultiCaches注解的接口", penetrationsBeanClassName);
            }
            penetrationsBeanType = Class.forName(penetrationsBeanClassName, true, this.beanFactory.getBeanClassLoader());
            try {
                penetrationsBean = penetrationsBeanType.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new CollapsarException(e, "实例化@Penetrations Bean，请排查构造方法是否有问题");
            }
            autowireBeans.add(penetrationsBean);
            for (String cachesInterfaceName : cachesInterfaceNames) {
                cachesInterfaceType = Class.forName(cachesInterfaceName, true, this.beanFactory.getBeanClassLoader());
                if (!cachesInterfaceType.isAnnotationPresent(Caches.class) && !cachesInterfaceType.isAnnotationPresent(MultiCaches.class)) {
                    continue;
                }
                if (penetrationsBeanMap.containsKey(cachesInterfaceName)) {
                    throw new CollapsarException("无法解析Bean[%s],@Caches/@MultiCaches注解接口[%s]已经存在@Penetrations实例", penetrationsBeanClassName, cachesInterfaceName);
                }
                penetrationsBeanMap.put(cachesInterfaceName, penetrationsBean);
            }
        }
        return penetrationsBeanMap;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //root application context 没有parent，他就是老大.
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
            if (!CollectionUtils.isEmpty(autowireBeans)) {
                for (Object autowireBean : autowireBeans) {
                    beanFactory.autowireBean(autowireBean);
                }
            }
        }
    }
}

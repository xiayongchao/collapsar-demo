package org.jc.framework.collapsar.core;


import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.jc.framework.collapsar.annotation.*;
import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.CollapsarComponentScanDefinition;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.definition.MultiCachesBeanDefinition;
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
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiayc
 * @date 2019/3/25
 */
public class CollapsarMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor, BeanFactoryAware, ApplicationListener<ContextRefreshedEvent> {
    private DefaultListableBeanFactory beanFactory;
    private final CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser;
    private final MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser;
    private final CollapsarBeanMethodHandler collapsarBeanMethodHandler;
    private final Set<String> collapsarBeans = new HashSet<>();
    private final Set<Object> autowireBeans = new HashSet<>();

    CollapsarMergedBeanDefinitionPostProcessor(CachesBeanDefinitionScanParser cachesBeanDefinitionScanParser,
                                               MultiCachesBeanDefinitionScanParser multiCachesBeanDefinitionScanParser,
                                               CollapsarBeanMethodHandler collapsarBeanMethodHandler) {
        this.cachesBeanDefinitionScanParser = cachesBeanDefinitionScanParser;
        this.multiCachesBeanDefinitionScanParser = multiCachesBeanDefinitionScanParser;
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
                this.registerCollapsarBeans(cachesBeanDefinitions, multiCachesBeanDefinitions);
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
                                        final Set<MultiCachesBeanDefinition> multiCachesBeanDefinitions) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (!CollectionUtils.isEmpty(cachesBeanDefinitions)) {
            for (CachesBeanDefinition cachesBeanDefinition : cachesBeanDefinitions) {
                registerCollapsarBean(cachesBeanDefinition.getBeanName(), cachesBeanDefinition.getBeanClassName(),
                        Caches.class, new MethodDefinition(cachesBeanDefinition));
            }
        }
        if (!CollectionUtils.isEmpty(multiCachesBeanDefinitions)) {
            for (MultiCachesBeanDefinition multiCachesBeanDefinition : multiCachesBeanDefinitions) {
                registerCollapsarBean(multiCachesBeanDefinition.getBeanName(), multiCachesBeanDefinition.getBeanClassName(),
                        MultiCaches.class, new MethodDefinition(multiCachesBeanDefinition));
            }
        }
    }

    private void registerCollapsarBean(String beanName, String beanClassName, Class<? extends Annotation> annotationClass, MethodDefinition methodDefinition) throws
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
            if (!isCacheOperate(method)) {
                continue;
            }
            collapsarBeanMethodHandler.registerMethod(method, methodDefinition);
        }

        autowireBeans.add(object);
        //将生成的对象注册到Spring容器
        this.beanFactory.registerSingleton(beanName, object);
    }

    private boolean isCacheOperate(Method method) {
        if (method == null) {
            return false;
        }
        return isCacheOperate(method.getDeclaredAnnotations());
    }

    private boolean isCacheOperate(Annotation[] annotations) {
        if (ArrayUtils.isEmpty(annotations)) {
            return false;
        }

        for (Annotation annotation : annotations) {
            if (annotation == null) {
                continue;
            }
            if (annotation.annotationType().isAnnotationPresent(CacheOperate.class)) {
                return true;
            }
            if (isCacheOperate(annotation.annotationType().getDeclaredAnnotations())) {
                return true;
            }
        }
        return false;
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

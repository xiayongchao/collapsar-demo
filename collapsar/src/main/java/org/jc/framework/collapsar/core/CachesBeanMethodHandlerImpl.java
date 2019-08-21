package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.MethodDefinition;

import java.lang.reflect.Method;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class CachesBeanMethodHandlerImpl implements CachesBeanMethodHandler {

    /**
     * 注册方法
     *
     * @param method
     * @param cachesBeanDefinition
     */
    @Override
    public void registerMethod(Method method, CachesBeanDefinition cachesBeanDefinition) {
        CacheKeyGenerator cacheKeyGenerator = new CacheKeyGenerator();
        cacheKeyGenerator.setProjectName(cachesBeanDefinition.getProjectName());
        cacheKeyGenerator.setModuleName(cachesBeanDefinition.getModuleName());
        cacheKeyGenerator.setConnector(cachesBeanDefinition.getConnector());

        MethodDefinition methodDefinition = CachesMethodParser.parseMethod(method, cachesBeanDefinition.getTargetType());

        System.out.println(method.getName());
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed,
                         Object[] args) throws Throwable {

        return null;
    }
}

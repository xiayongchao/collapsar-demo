package org.jc.framework.collapsar.core;


import org.jc.framework.collapsar.definition.CachesBeanDefinition;
import org.jc.framework.collapsar.definition.MethodDefinition;

import java.lang.reflect.Method;

import static org.jc.framework.collapsar.constant.Operate.*;

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

        String className = thisMethod.getDeclaringClass().getName();
        String methodName = thisMethod.getName();
        String operateRuleKey = getOperateRuleKey(className, methodName);
        OperateRuleDefinition operateRuleDefinition = operateRuleDefinitionMap.get(operateRuleKey);
        if (operateRuleDefinition == null) {
            throw CommentUtils.getRuntimeException("未注册的缓存方法:%s#%s()", className, methodName);
        }
        switch (operateRuleDefinition.getOperateType()) {
            case SET:
                return cacheRepository.setObject(operateRuleDefinition.getCacheKey(objects), objects[operateRuleDefinition.getValueParamIndex()],
                        operateRuleDefinition.getExpire(), operateRuleDefinition.isAtomic());
            case GET:
                return cacheRepository.getObject(operateRuleDefinition.getCacheKey(objects), thisMethod.getReturnType());
            case DEL:
                return cacheRepository.removeObject(operateRuleDefinition.getCacheKey(objects));
            default:
                throw CommentUtils.getRuntimeException("未知的方法操作类型:%s#%s():%s", className, methodName, operateRuleDefinition.getOperateType().getValue());
        }
    }
}

package org.jc.framework.collapsar.support.invoker;

import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.extend.CacheRepository;
import org.jc.framework.collapsar.support.CachesDelMethod;
import org.jc.framework.collapsar.support.CachesMethodWrapper;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public abstract class MethodInvoker {
    protected final CacheRepository cacheRepository;

    /**
     * 下一个处理器
     */
    private MethodInvoker nextInvoker;

    protected abstract Object invoke(Object[] args, CachesMethodWrapper cachesMethodWrapper) throws InvocationTargetException, IllegalAccessException;

    public MethodInvoker getNextInvoker() {
        return nextInvoker;
    }

    public void setNextInvoker(MethodInvoker nextInvoker) {
        this.nextInvoker = nextInvoker;
    }

    protected MethodInvoker(CacheRepository cacheRepository) {
        if (cacheRepository == null) {
            throw new CollapsarException("使用Collapsar必须提供[%s]的Spring Bean实例", CacheRepository.class.getName());
        }
        this.cacheRepository = cacheRepository;
    }

    public final static MethodInvoker init(CacheRepository cacheRepository) {

        return null;
    }
}

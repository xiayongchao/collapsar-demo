package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/28 0:26
 */
public class CachesMethod implements CachesSetMethod, CachesGetMethod, CachesDelMethod, CachesBatchDelMethod {
    private final String cacheKeyTemplate;
    private final String methodFullName;
    private Operate operate;
    private ParameterKeyBuilder[] parameterKeyBuilders;
    private Class<?> returnType;
    /**
     * 缓存过期时间
     */
    private long expire = 0;
    private int valueParameterIndex = -1;

    public CachesMethod(String projectName, String moduleName, String connector, String methodFullName) {
        this.cacheKeyTemplate = projectName + connector + moduleName + connector + "%s_%s";
        this.methodFullName = methodFullName;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }

    public ParameterKeyBuilder[] getParameterKeyBuilders() {
        return parameterKeyBuilders;
    }

    public void setParameterKeyBuilders(ParameterKeyBuilder[] parameterKeyBuilders) {
        this.parameterKeyBuilders = parameterKeyBuilders;
    }

    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    @Override
    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public int getValueParameterIndex() {
        return valueParameterIndex;
    }

    public void setValueParameterIndex(int valueParameterIndex) {
        this.valueParameterIndex = valueParameterIndex;
    }

    @Override
    public Object selectValueParameter(Object[] args) {
        return args[valueParameterIndex];
    }

    @Override
    public String generateKey(Object[] args) {
        StringJoiner keyNameJoiner = new StringJoiner("&");
        StringJoiner keyValueJoiner = new StringJoiner("&");
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            keyNameJoiner.add(parameterKeyBuilder.getName());
            keyValueJoiner.add(parameterKeyBuilder.buildKey(args));
        }
        return String.format(cacheKeyTemplate, keyNameJoiner.toString(), keyValueJoiner.toString());
    }

    @Override
    public Object[] filterArgs(int i, Object[] args) {
        Object[] filterArgs = new Object[args.length];
        System.arraycopy(args, 0, filterArgs, 0, args.length);
        Object arg;
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            arg = args[parameterKeyBuilder.getIndex()];
            if (parameterKeyBuilder.isBatch()) {
                filterArgs[parameterKeyBuilder.getIndex()] = arg == null ? null : ((List) arg).get(i);
            }
        }
        return filterArgs;
    }

    @Override
    public int calcListSize(Object[] args) {
        Object arg;
        Integer size = null;
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            arg = args[parameterKeyBuilder.getIndex()];
            if (parameterKeyBuilder.isBatch()) {
                if (size == null) {
                    size = arg == null ? 0 : ((List) arg).size();
                } else if (size != (arg == null ? 0 : ((List) arg).size())) {
                    throw new CollapsarException("方法[%s]执行失败,提供的多个List集合参数的size需要保持一致", methodFullName);
                }
            }
        }
        return size == null ? 0 : size;
    }
}

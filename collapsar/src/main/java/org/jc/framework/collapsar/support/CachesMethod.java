package org.jc.framework.collapsar.support;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.support.builder.ParameterKeyBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author jc
 * @date 2019/8/28 0:26
 */
public class CachesMethod implements CachesNoneMethod, CachesSetMethod, CachesGetMethod, CachesDelMethod,
        CachesBatchDelMethod, CachesBatchGetMethod, CachesBatchSetMethod {
    private String projectName;
    private String moduleName;
    private String connector;
    private String methodFullName;
    private Operate operate;
    private ParameterKeyBuilder[] parameterKeyBuilders;
    private Type returnType;
    /**
     * 缓存过期时间
     */
    private long expire = 0;
    private int valueParameterIndex = -1;
    private Class<? extends List> implType;
    private Object penetrationsBean;
    private Method penetrationsMethod;

    public CachesMethod(String projectName, String moduleName, String connector, String methodFullName) {
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.connector = connector;
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

    public void setParameterKeyBuilders(ParameterKeyBuilder[] parameterKeyBuilders) {
        this.parameterKeyBuilders = parameterKeyBuilders;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setValueParameterIndex(int valueParameterIndex) {
        this.valueParameterIndex = valueParameterIndex;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public Object selectValueParameter(Object[] args) {
        return args[valueParameterIndex];
    }

    @Override
    public Object selectValueParameter(int i, Object[] args) {
        List arg = (List) args[valueParameterIndex];
        return arg == null ? null : arg.get(i);
    }

    @Override
    public String generateKey(Object[] args) {
        StringJoiner keyNameJoiner = new StringJoiner("&");
        StringJoiner keyValueJoiner = new StringJoiner("&");
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            keyNameJoiner.add(parameterKeyBuilder.getName());
            keyValueJoiner.add(parameterKeyBuilder.buildKey(args));
        }
        return String.format("%s%s%s%s%s_%s", projectName, connector, moduleName,
                connector, keyNameJoiner.toString(), keyValueJoiner.toString());
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
    public Object[] filterArgs(List<Integer> indexList, Object[] args) {
        Object[] filterArgs = new Object[args.length];
        System.arraycopy(args, 0, filterArgs, 0, args.length);
        Object arg;
        Iterator iterator;
        int i;
        for (ParameterKeyBuilder parameterKeyBuilder : parameterKeyBuilders) {
            arg = filterArgs[parameterKeyBuilder.getIndex()];
            if (parameterKeyBuilder.isBatch()) {
                iterator = arg == null ? null : ((List) arg).iterator();
                i = 0;
                while (arg != null && iterator.hasNext()) {
                    iterator.next();
                    if (indexList != null && !indexList.contains(i)) {
                        iterator.remove();
                    }
                    i++;
                }
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

    @Override
    public Class<? extends List> getImplType() {
        return implType;
    }

    public void setImplType(Class<? extends List> implType) {
        this.implType = implType;
    }

    public void setPenetrationsBean(Object penetrationsBean) {
        this.penetrationsBean = penetrationsBean;
    }

    public void setPenetrationsMethod(Method penetrationsMethod) {
        this.penetrationsMethod = penetrationsMethod;
    }

    public Object invokePenetrationMethod(Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (penetrationsBean == null || penetrationsMethod == null) {
            return null;
        }
        return penetrationsMethod.invoke(penetrationsBean, args);
    }
}

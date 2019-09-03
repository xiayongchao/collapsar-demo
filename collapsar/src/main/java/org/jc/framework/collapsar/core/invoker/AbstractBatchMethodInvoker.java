package org.jc.framework.collapsar.core.invoker;

import org.jc.framework.collapsar.exception.CollapsarException;
import org.jc.framework.collapsar.core.builder.ParameterKeyBuilder;

import java.util.List;

/**
 * @author jc
 * @date 2019/8/30 1:01
 */
public abstract class AbstractBatchMethodInvoker extends AbstractMethodInvoker {
    protected int calcListSize(Object[] args) {
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

    protected Object[] filterArgs(int i, Object[] args) {
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
}

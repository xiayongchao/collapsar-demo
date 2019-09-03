package org.jc.framework.collapsar.core.parser;

import org.jc.framework.collapsar.constant.Operate;
import org.jc.framework.collapsar.definition.MethodDefinition;
import org.jc.framework.collapsar.core.invoker.MethodInvoker;
import org.jc.framework.collapsar.core.invoker.OrdinaryMethodInvoker;

import java.lang.reflect.Method;

/**
 * 普通方法解析器
 *
 * @author jc
 * @date 2019/8/26 21:58
 */
public class OrdinaryMethodParser extends MethodParser {
    private final OrdinaryMethodInvoker methodInvoker = new OrdinaryMethodInvoker();

    public OrdinaryMethodParser(Method method, MethodDefinition methodDefinition) {
        super(Operate.NONE, method, methodDefinition);
    }

    @Override
    public MethodInvoker getMethodInvoker() {
        return methodInvoker;
    }
}

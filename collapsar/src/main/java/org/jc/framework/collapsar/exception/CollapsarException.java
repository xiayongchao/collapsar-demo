package org.jc.framework.collapsar.exception;

/**
 * @author xiayc
 * @date 2019/3/25
 */
public class CollapsarException extends RuntimeException {
    public CollapsarException() {
    }

    public CollapsarException(String template, Object... messages) {
        super(String.format(template, messages));
    }

    public CollapsarException(Throwable cause, String template, Object... messages) {
        super(String.format(template, messages), cause);
    }

    public CollapsarException(Throwable cause) {
        super(cause);
    }
}

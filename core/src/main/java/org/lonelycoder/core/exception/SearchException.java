package org.lonelycoder.core.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * @author : lihaoquan
 */
public class SearchException extends NestedRuntimeException {

    public SearchException(String msg) {
        super(msg);
    }

    public SearchException(String msg, Throwable throwable) {
        super(msg,throwable);
    }
}

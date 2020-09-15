package com.zandero.rest.test.exceptions;

/**
 *
 */
public class InheritedBaseException extends BaseException {

    public InheritedBaseException(String message) {
        super("INHERITED: " + message);
    }
}

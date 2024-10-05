package com.zandero.resttest.test.exceptions;

/**
 *
 */
public class InheritedBaseException extends BaseException {

    public InheritedBaseException(String message) {
        super("INHERITED: " + message);
    }
}

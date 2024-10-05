package com.zandero.resttest.test.exceptions;

/**
 *
 */
public class BaseException extends Throwable {

    public BaseException(String message) {
        super("BASE: " + message);
    }
}

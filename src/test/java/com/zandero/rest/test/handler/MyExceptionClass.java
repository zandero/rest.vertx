package com.zandero.rest.test.handler;

/**
 *
 */
public class MyExceptionClass extends Throwable {

    private final String error;
    private final int status;

    public MyExceptionClass(String message, int code) {
        error = message;
        status = code;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }
}

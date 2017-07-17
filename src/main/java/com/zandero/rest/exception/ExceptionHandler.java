package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;

/**
 *
 */
public interface ExceptionHandler<T extends Throwable> extends HttpResponseWriter<T> {

}

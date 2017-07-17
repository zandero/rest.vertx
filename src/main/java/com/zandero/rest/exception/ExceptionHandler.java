package com.zandero.rest.exception;

import com.zandero.rest.writer.HttpResponseWriter;

/**
 * Specialized writer interface for exception handling
 */
public interface ExceptionHandler<T extends Throwable> extends HttpResponseWriter<T> {

}

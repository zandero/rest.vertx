package com.zandero.rest.annotation;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.exception.GenericExceptionHandler;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CatchWith {

	Class<? extends ExceptionHandler>[] value() default GenericExceptionHandler.class;

	//Class<? extends ExceptionHandler>[] value()[] default

	/**
	 * @return alternative response writer, provided in handle() when ExceptionHandler is invoked
	 */
	Class<? extends HttpResponseWriter> writer() default NotImplementedWriter.class;

	final class NotImplementedWriter implements HttpResponseWriter {
		@Override public void write(Object result, HttpServerRequest request, HttpServerResponse response) {
			throw new IllegalStateException("Not intended for direct use. For annotation purposes only!");
		}
	}
}

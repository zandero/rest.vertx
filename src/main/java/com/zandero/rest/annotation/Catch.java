package com.zandero.rest.annotation;

import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.exception.GenericExceptionHandler;
import com.zandero.rest.writer.HttpResponseWriter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Catch {

	Class<? extends ExceptionHandler> value() default GenericExceptionHandler.class;

	/**
	 * @return alternative response writer, provided in handle() when ExceptionHandler is invoked
	 */
	Class<? extends HttpResponseWriter> writer();
}

package com.zandero.rest.annotation;

import com.zandero.rest.writer.HttpResponseWriter;

import java.lang.annotation.*;

/**
 * Indicates that the output will be written with given HttpResponseWriter
 * allows routing of method result through a specific response writer
 *
 * @see HttpResponseWriter
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWriter {

	Class<? extends HttpResponseWriter> value();
}

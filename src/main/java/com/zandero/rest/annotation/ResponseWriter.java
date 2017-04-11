package com.zandero.rest.annotation;

/**
 * Indicates that the output will be written with given HttpResponseWriter
 * allows routing of method result through a specific response writer
 *
 * @see HttpResponseWriter
 */

import com.zandero.rest.writer.HttpResponseWriter;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWriter {

	Class<HttpResponseWriter> value();
}

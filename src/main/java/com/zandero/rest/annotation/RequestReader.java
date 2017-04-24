package com.zandero.rest.annotation;

/**
 * Indicates that the request body will be read by HttpRequestBodyReader
 * allows routing of method request through a specific reader
 *
 * @see com.zandero.rest.reader.HttpRequestBodyReader
 */

import com.zandero.rest.reader.HttpRequestBodyReader;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestReader {

	Class<? extends HttpRequestBodyReader> value();
}

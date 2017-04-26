package com.zandero.rest.annotation;


import com.zandero.rest.reader.HttpRequestBodyReader;

import java.lang.annotation.*;

/**
 * Indicates that the request body will be read by HttpRequestBodyReader
 * allows routing of method request through a specific reader
 *
 * @see com.zandero.rest.reader.HttpRequestBodyReader
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestReader {

	Class<? extends HttpRequestBodyReader> value();
}

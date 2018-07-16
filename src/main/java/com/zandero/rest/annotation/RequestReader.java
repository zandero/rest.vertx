package com.zandero.rest.annotation;

import com.zandero.rest.reader.ValueReader;

import java.lang.annotation.*;

/**
 * Indicates that the request body will be read by ValueReader
 * allows routing of method request through a specific reader
 *
 * @see ValueReader
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestReader {

	Class<? extends ValueReader> value();
}

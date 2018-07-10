package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * Applies to executeBlocking only (provide if blocking is desired, otherwise not needed)
 * true to block execution, request are executed serially, false to execute parallel on pool (default)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Blocking {

	boolean value() default true;
}

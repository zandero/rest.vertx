package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * The REST call block async execution
 * same as calling vertx.executeBlocking()
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Blocking {

	boolean value() default true;
}

package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * The REST call block async execution
 * same as calling vertx.executeBlocking()
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated // all handlers are by default blocking to ... additional async logic will handle non blocking requests
public @interface Blocking {

	boolean value() default true;
}

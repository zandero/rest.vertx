package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * In case routes need to be ordered in a specific way
 * the lower the value the earlier a route will be bound
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouteOrder {

	int value() default 0;
}

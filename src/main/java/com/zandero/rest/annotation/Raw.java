package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * Applies to any parameters that is transformed via some internal logic and disables transformation
 * providing the raw value as it is delivered by vert.x
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Raw {

	boolean value() default true;
}

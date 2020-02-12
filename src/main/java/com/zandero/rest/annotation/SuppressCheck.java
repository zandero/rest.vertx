package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * Suppresses type compatibility checks
 * for HttpRequestReader and HttpResponseWriter classes
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SuppressCheck {

    String value() default "";
}

package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * Will provide body content of request to bean
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {
}

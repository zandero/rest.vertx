package com.zandero.rest.annotation;

import java.lang.annotation.*;

/**
 * Disable class instance caching
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoCache {
}

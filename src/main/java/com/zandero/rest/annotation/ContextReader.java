package com.zandero.rest.annotation;

import com.zandero.rest.context.ContextProvider;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ContextReader {
    Class<? extends ContextProvider> value();
}

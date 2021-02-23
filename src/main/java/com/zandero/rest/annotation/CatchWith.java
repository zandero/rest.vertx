package com.zandero.rest.annotation;

import com.zandero.rest.exception.*;

import java.lang.annotation.*;

/**
 *  Provides exception handler for a thrown exception type, for a specific class or REST method
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface CatchWith {

    /**
     * One or more exception handler to handle given exception types.
     * List handlers in order they should be considered, first match is used
     *
     * @return list of exception handlers, or default exception handler if none associated
     */
    Class<? extends ExceptionHandler>[] value() default GenericExceptionHandler.class;
}

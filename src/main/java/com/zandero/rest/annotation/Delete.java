package com.zandero.rest.annotation;

import jakarta.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 * Delete HTTP method alternative
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("DELETE")
@Documented
public @interface Delete {

    String value() default ""; // same as @Path

    String[] produces() default "*/*"; // same as @Produces

    String[] consumes() default "*/*"; // same as @Consumes
}

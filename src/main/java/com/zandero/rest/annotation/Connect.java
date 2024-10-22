package com.zandero.rest.annotation;

import jakarta.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 * Connect HTTP method alternative
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("CONNECT")
@Documented
public @interface Connect {

    String value() default ""; // same as @Path

    String[] produces() default "*/*"; // same as @Produces

    String[] consumes() default "*/*"; // same as @Consumes
}


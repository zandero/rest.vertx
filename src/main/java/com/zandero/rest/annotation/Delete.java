package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 *
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

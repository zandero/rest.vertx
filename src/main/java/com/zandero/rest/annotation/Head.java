package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 * Head HTTP method alternative
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("HEAD")
@Documented
public @interface Head {

	String value() default ""; // same as @Path

	String[] produces() default "*/*"; // same as @Produces

	String[] consumes() default "*/*"; // same as @Consumes
}

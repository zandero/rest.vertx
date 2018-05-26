package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
@Documented
public @interface Patch {

	String value() default ""; // same as @Path

	String[] produces() default "*/*"; // same as @Produces

	String[] consumes() default "*/*"; // same as @Consumes
}

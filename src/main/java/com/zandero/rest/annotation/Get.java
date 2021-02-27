package com.zandero.rest.annotation;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

/**
 * Get HTTP method alternative
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("GET")
@Documented
public @interface Get {

	String value() default ""; // same as @Path

	String[] produces() default "*/*"; // same as @Produces

	String[] consumes() default "*/*"; // same as @Consumes

	// TODO: add String[] headers() default "" to complete
}

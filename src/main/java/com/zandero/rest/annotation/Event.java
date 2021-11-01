package com.zandero.rest.annotation;

import com.zandero.rest.events.RestEvent;

import java.lang.annotation.*;

/**
 * Single event to be triggered when REST call has been executed
 * after response writer has been invoked
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

	int DEFAULT_EVENT_STATUS = -1;	// will be triggered on OK responses
	int ON_ALL = -2;				// will always be triggered

	/**
	 * event processor class to execute given event
	 * @return processor to execute upon response
	 */
	Class<? extends RestEvent> value(); // processor class (execution of action)

	/**
	 * response code to react upon
	 * @return response code to bind event to or default to trigger every time
	 */
	int response() default DEFAULT_EVENT_STATUS; // response code to react to (-1 = ALL) - rest response to react upon (http status code)

	/**
	 * In case response is null / empty event will not be triggered, unless stated otherwise
	 * @return if event should be triggered on empty response
	 */
	boolean onEmpty() default false;

	/**
	 * exception to react upon if any
	 * @return exception error to bind event to
	 */
	Class<? extends Throwable> exception() default RestEvent.NoRestException.class; // default to a "not" exception (same as null)
}
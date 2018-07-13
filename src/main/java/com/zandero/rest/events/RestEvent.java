package com.zandero.rest.events;

import io.vertx.ext.web.RoutingContext;

/**
 * Triggered when REST call is complete ...
 * T being the entity produced by the original method OR writer producing the response
 */
public interface RestEvent<T> {

	void execute(T entity, RoutingContext context) throws Throwable;
}

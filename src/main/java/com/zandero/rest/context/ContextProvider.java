package com.zandero.rest.context;

import io.vertx.core.http.HttpServerRequest;

/**
 *
 */
public interface ContextProvider<T> {

	/**
	 * @param request current request
	 * @return object to be pushed into context storage
	 */
	T provide(HttpServerRequest request);
}

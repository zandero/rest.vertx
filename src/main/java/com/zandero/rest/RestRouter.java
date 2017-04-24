package com.zandero.rest;

import com.zandero.rest.data.ArgumentProvider;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.reader.HttpRequestBodyReader;
import com.zandero.rest.reader.ReaderFactory;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.rest.writer.WriterFactory;
import com.zandero.utils.Assert;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	private static final WriterFactory writers = new WriterFactory();

	private static final ReaderFactory readers = new ReaderFactory();

	/**
	 * Searches for annotations to register routes ...
	 *
	 * @param vertx   Vert.X instance
	 * @param restApi instance to search for annotations
	 * @return Router new Router with routes as defined in {@code restApi} class
	 */
	public static Router register(Vertx vertx, Object... restApi) {

		Assert.notNull(vertx, "Missing vertx!");
		Assert.isTrue(restApi != null && restApi.length > 0, "Missing REST API class object!");

		Router router = Router.router(vertx);
		return register(router, restApi);
	}

	/**
	 * Searches for annotations to register routes ...
	 *
	 * @param restApi instance to search for annotations
	 * @param router  to add additional routes from {@code restApi} class
	 * @return Router with routes as defined in {@code restApi} class
	 */
	public static Router register(Router router, Object... restApi) {

		Assert.notNull(router, "Missing vert.x router!");

		for (Object api : restApi) {

			Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(api.getClass());

			boolean bodyHandlerRegistered = false;

			for (RouteDefinition definition : definitions.keySet()) {

				Method method = definitions.get(definition);

				// add BodyHandler in case request has a body ...
				if (definition.requestHasBody() && !bodyHandlerRegistered) {
					router.route().handler(BodyHandler.create());
					bodyHandlerRegistered = true;
				}

				// bind method execution
				Route route;
				if (definition.pathIsRegEx()) {
					route = router.routeWithRegex(definition.getMethod(), definition.getRoutePath());
				}
				else {
					route = router.route(definition.getMethod(), definition.getRoutePath());
				}

				log.info("Registering route: " + definition);

				if (definition.getConsumes() != null) {
					for (MediaType item : definition.getConsumes()) {
						route.consumes(MediaTypeHelper.getKey(item)); // remove charset when binding
					}
				}

				if (definition.getProduces() != null) {
					for (MediaType item : definition.getProduces()) {
						route.produces(MediaTypeHelper.getKey(item)); // remove charset when binding
					}
				}

				route.handler(getHandler(api, definition, method));
			}
		}

		return router;
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {

				HttpRequestBodyReader argumentConverter = readers.getRequestBodyReader(method.getReturnType(), definition);
				Object[] args = ArgumentProvider.getArguments(method, definition, context, argumentConverter);

				Object result = method.invoke(toInvoke, args);

				HttpServerResponse response = context.response();
				HttpServerRequest request = context.request();

				// find suitable writer to produce response
				HttpResponseWriter writer = writers.getResponseWriter(method.getReturnType(), definition);

				// add default response headers per definition
				writer.addResponseHeaders(definition, response);

				// write response and override headers if necessary
				writer.write(result, request, response);

				// finish if not finished by writer
				if (!response.ended()) {
					response.end();
				}
			}
			catch (IllegalArgumentException e) { // TODO: replace with custom exception
				log.error("Invalid parameters provided: " + e.getMessage(), e);
				context.response().setStatusCode(400).end(e.getMessage());
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				// return 500 error with stack trace
				// e.printStackTrace();
				log.error("Failed to call: " + method.getName() + " " + e.getMessage(), e);
				context.response().setStatusCode(500).end(e.getMessage());
			}
		};
	}

	public static WriterFactory getWriters() {

		return writers;
	}

	public static ReaderFactory getReaders() {

		return readers;
	}
}

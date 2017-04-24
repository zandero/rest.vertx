package com.zandero.rest;

import com.zandero.rest.data.ArgumentProvider;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.ExecuteException;
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

				if (definition.isBlocking()) {

					context.vertx().executeBlocking(future -> {

						Object result = execute(method, toInvoke, args);
						future.complete(result);

					}, res -> {

						if (res.succeeded()) {

							produceResponse(res.result(), context, method, definition);
						}
						else {

							ExecuteException ex = new ExecuteException(500, res.cause());
							produceResponse(ex, context, method, definition);
						}
					});
				}
				else {

					Object result = execute(method, toInvoke, args);
					produceResponse(result, context, method, definition);
				}
			}
			catch (IllegalArgumentException e) {

				ExecuteException ex = new ExecuteException(400, e);
				produceResponse(ex, context, method, definition);
			}
		};
	}

	private static Object execute(Method method, Object toInvoke, Object[] arguments) {

		try {
			return method.invoke(toInvoke, arguments);
		}
		catch (IllegalArgumentException e) {
			return new ExecuteException(400, e);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			return new ExecuteException(500, e);
		}
	}

	private static void produceResponse(Object result, RoutingContext context, Method method, RouteDefinition definition) {

		HttpServerResponse response = context.response();

		if (result instanceof ExecuteException) {

			ExecuteException exception = (ExecuteException) result;
			log.error("Failed to invoke method: " + method + ", " + exception.getMessage(), exception);

			response.setStatusCode(exception.getStatusCode()).end(exception.getMessage());
		}
		else {

			HttpServerRequest request = context.request();

			// find suitable writer to produce response
			HttpResponseWriter writer;

			writer = writers.getResponseWriter(method.getReturnType(), definition);
			// add default response headers per definition
			writer.addResponseHeaders(definition, response);

			// write response and override headers if necessary
			writer.write(result, request, response);

			// finish if not finished by writer
			if (!response.ended()) {
				response.end();
			}
		}
	}

	public static WriterFactory getWriters() {

		return writers;
	}

	public static ReaderFactory getReaders() {

		return readers;
	}
}

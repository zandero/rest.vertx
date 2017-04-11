package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.writer.GenericResponseWriter;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.rest.writer.JaxResponseWriter;
import com.zandero.rest.writer.NoContentResponseWriter;
import com.zandero.utils.Assert;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	// map of writers
	private static Map<Class, Class<? extends HttpResponseWriter>> WRITERS;
	static {
		WRITERS = new HashMap<>();
		WRITERS.put(Response.class, JaxResponseWriter.class);
		WRITERS.put(String.class, GenericResponseWriter.class);
	}

	/**
	 * Searches for annotations to register routes ...
	 *
	 * @param vertx   Vert.X instance
	 * @param restApi instance to search for annotations
	 * @return Router new Router with routes as defined in {@code restApi} class
	 */
	public static Router register(Vertx vertx, Object restApi) {

		Assert.notNull(vertx, "Missing vertx!");
		Assert.notNull(restApi, "Missing REST API class object!");

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
	public static Router register(Router router, Object restApi) {

		Assert.notNull(router, "Missing vert.x router!");

		Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(restApi.getClass());

		for (RouteDefinition definition : definitions.keySet()) {

			Method method = definitions.get(definition);

			Route route = router.route(definition.getMethod(), definition.getPath());
			log.info("Registering route: " + definition);

			if (definition.getConsumes() != null) {
				for (String item : definition.getConsumes()) {
					route.consumes(item);
				}
			}

			if (definition.getProduces() != null) {
				for (String item : definition.getProduces()) {
					route.produces(item);
				}
			}

			// bind method execution
			route.handler(getHandler(restApi, definition, method));
		}

		return router;
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {

				// todo ... invoke correctly with @PathParam and @QueryParams in correct place
				Object result = method.invoke(toInvoke);

				// dummy response ... as proof of concept
				// TODO: add response builder according to definition produces

				HttpServerResponse response = context.response();

				HttpResponseWriter writer = getResponseWriter(method.getReturnType(), definition);

				// add default response headers
				writer.addResponseHeaders(definition, response);

				// write response and override headers if necessary
				writer.write(result, response);

				// finish
				if (!response.ended()) {
					response.end();
				}
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				// return 500 error with stack trace
				// e.printStackTrace();
				context.response().setStatusCode(500).end(e.getMessage());
			}
		};
	}

	// TODO ... consider result type of method not result as it is ...
	private static HttpResponseWriter getResponseWriter(Class<?> returnType, RouteDefinition definition) {

		Class<? extends HttpResponseWriter> writer = definition.getWriter();

		if (writer == null) {

			if (returnType == null) {
				writer = NoContentResponseWriter.class;
			}
			else {
				// try to find appropriate writer if mapped
				for (Class clazz : WRITERS.keySet()) {

					if (returnType.isInstance(clazz)) {
						writer = WRITERS.get(clazz);
					}
				}
			}
		}

		if (writer != null) {

			// create writer instance
			try {
				return writer.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				log.error("Failed to instantiate response writer '" + writer.getName() + "' " + e.getMessage(), e);
				// TODO: probably best to throw exception here
			}
		}

		// fall back to generic writer ...
		return new GenericResponseWriter();
	}
}

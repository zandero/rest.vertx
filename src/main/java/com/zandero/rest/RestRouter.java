package com.zandero.rest;

import com.zandero.rest.data.ArgumentProvider;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.writer.*;
import com.zandero.utils.Assert;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	// map of writers by class type
	private static Map<String, Class<? extends HttpResponseWriter>> CLASS_TYPE_WRITERS = new HashMap<>();

	// map of writers by response content type / media type
	private static Map<String, Class<? extends HttpResponseWriter>> MEDIA_TYPE_WRITERS = new HashMap<>();

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

		initWriters();

		Assert.notNull(router, "Missing vert.x router!");

		for (Object api: restApi) {

			Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(api.getClass());

			boolean bodyHandlerRegistered = false;

			for (RouteDefinition definition : definitions.keySet()) {

				Method method = definitions.get(definition);

				// bind method execution
				if (definition.requestHasBody() && !bodyHandlerRegistered) {
					router.route().handler(BodyHandler.create());
					bodyHandlerRegistered = true;
				}

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

				route.handler(getHandler(api, definition, method));
			}
		}

		return router;
	}

	private static void initWriters() {

		if (CLASS_TYPE_WRITERS.size() == 0 &&
			MEDIA_TYPE_WRITERS.size() == 0) {

			CLASS_TYPE_WRITERS.put(Response.class.getName(), JaxResponseWriter.class);
			MEDIA_TYPE_WRITERS.put(getMediaTypeKey(MediaType.APPLICATION_JSON_TYPE), JsonResponseWriter.class);
		}
	}

	public static void clear() {

		// clears any additionally registered writers and initializes defaults
		MEDIA_TYPE_WRITERS.clear();
		CLASS_TYPE_WRITERS.clear();

		initWriters();
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {

				Object[] args = ArgumentProvider.getArguments(definition, context);
				Object result = method.invoke(toInvoke, args);

				HttpServerResponse response = context.response();

				// find suitable writer to produce response
				HttpResponseWriter writer = getResponseWriter(method.getReturnType(), definition);

				// add default response headers per definition
				writer.addResponseHeaders(definition, response);

				// write response and override headers if necessary
				writer.write(result, response);

				// finish if not finished by writer
				if (!response.ended()) {
					response.end();
				}
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				// return 500 error with stack trace
				// e.printStackTrace();
				log.error("Failed to call: " + method.getName() + " " + e.getMessage(), e);
				context.response().setStatusCode(500).end(e.getMessage());
			}
		};
	}

	public static void registerWriter(String mediaType, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer!");

		MediaType type = MediaType.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);
		String key = getMediaTypeKey(type);

		MEDIA_TYPE_WRITERS.put(key, writer);
	}

	public static void registerWriter(MediaType mediaType, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer!");

		String key = getMediaTypeKey(mediaType);
		MEDIA_TYPE_WRITERS.put(key, writer);
	}

	public static void registerWriter(Class<?> response, Class<? extends HttpResponseWriter> writer) {
		Assert.notNull(response, "Missing response class!");
		Assert.notNull(writer, "Missing response writer!");

		CLASS_TYPE_WRITERS.put(response.getName(), writer);
	}

	private static String getMediaTypeKey(MediaType mediaType) {

		return mediaType.getType() + "/" + mediaType.getSubtype();
	}

	/**
	 * Finds assigned response writer or tries to assign a writer according to produces annotation and result type
	 *
	 * @param returnType type of result
	 * @param definition method definition
	 * @return writer to be used to produce response, {@see GenericResponseWriter} in case no suitable writer could be found
	 */
	private static HttpResponseWriter getResponseWriter(Class<?> returnType, RouteDefinition definition) {

		Class<? extends HttpResponseWriter> writer = definition.getWriter();

		if (writer == null) {

			if (returnType == null) {
				writer = NoContentResponseWriter.class;
			}
			else {
				// try to find appropriate writer if mapped
				for (String clazz : CLASS_TYPE_WRITERS.keySet()) {

					if (clazz.equals(returnType.getName())) {
						writer = CLASS_TYPE_WRITERS.get(clazz);
						break;
					}
				}
			}
		}

		if (writer == null) { // try by produces

			String[] produces = definition.getProduces();
			if (produces != null && produces.length > 0) {
				MediaType mediaType = MediaType.valueOf(produces[0]);
				writer = getResponseWriter(mediaType);
			}
		}

		if (writer != null) {

			// create writer instance
			try {
				// TODO .. might be a good idea to cache writer instances for some time
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

	private static Class<? extends HttpResponseWriter> getResponseWriter(MediaType mediaType) {

		if (mediaType == null) {
			return null;
		}

		return MEDIA_TYPE_WRITERS.get(getMediaTypeKey(mediaType));
	}
}

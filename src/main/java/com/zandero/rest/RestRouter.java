package com.zandero.rest;

import com.zandero.rest.data.ArgumentProvider;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.reader.GenericBodyReader;
import com.zandero.rest.reader.HttpRequestBodyReader;
import com.zandero.rest.reader.JsonBodyReader;
import com.zandero.rest.writer.*;
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

	// map of readers by class type
	private static Map<String, Class<? extends HttpRequestBodyReader>> CLASS_TYPE_READERS = new HashMap<>();

	// map of readert by consumes / media type
	private static Map<String, Class<? extends HttpRequestBodyReader>> MEDIA_TYPE_READERS = new HashMap<>();

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
						route.consumes(getMediaTypeKey(item)); // remove charset when binding
					}
				}

				if (definition.getProduces() != null) {
					for (MediaType item : definition.getProduces()) {
						route.produces(getMediaTypeKey(item)); // remove charset when binding
					}
				}

				route.handler(getHandler(api, definition, method));
			}
		}

		return router;
	}

	private static void initWriters() {

		if (CLASS_TYPE_WRITERS.size() == 0) {

			CLASS_TYPE_READERS.put(String.class.getName(), GenericBodyReader.class);

			MEDIA_TYPE_READERS.put(MediaType.APPLICATION_JSON, JsonBodyReader.class);
			MEDIA_TYPE_READERS.put(MediaType.TEXT_PLAIN, GenericBodyReader.class);


			CLASS_TYPE_WRITERS.put(Response.class.getName(), JaxResponseWriter.class);

			MEDIA_TYPE_WRITERS.put(MediaType.APPLICATION_JSON, JsonResponseWriter.class);
			MEDIA_TYPE_WRITERS.put(MediaType.TEXT_PLAIN, GenericResponseWriter.class);
		}
	}

	public static void clear() {

		// clears any additionally registered writers and initializes defaults
		CLASS_TYPE_READERS.clear();
		MEDIA_TYPE_READERS.clear();

		CLASS_TYPE_WRITERS.clear();
		MEDIA_TYPE_WRITERS.clear();

		initWriters();
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {

				HttpRequestBodyReader argumentConverter = getRequestBodyReader(method.getReturnType(), definition);
				Object[] args = ArgumentProvider.getArguments(method, definition, context, argumentConverter);

				Object result = method.invoke(toInvoke, args);

				HttpServerResponse response = context.response();
				HttpServerRequest request = context.request();

				// find suitable writer to produce response
				HttpResponseWriter writer = getResponseWriter(method.getReturnType(), definition);

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

	/**
	 * Provides request body converter
	 *
	 * @param returnType method return type
	 * @param definition route definition
	 * @return reader to convert request body
	 */
	private static HttpRequestBodyReader getRequestBodyReader(Class<?> returnType, RouteDefinition definition) {

		Class<? extends HttpRequestBodyReader> reader = definition.getReader();

		// 2. if no writer is specified ... try to find appropriate writer by response type
		if (reader == null) {

			if (returnType != null) {
				// try to find appropriate writer if mapped
				reader = CLASS_TYPE_READERS.get(returnType.getName());
			}
		}

		if (reader == null) { // try by consumes

			MediaType[] consumes = definition.getConsumes();
			if (consumes != null && consumes.length > 0) {

				for (MediaType type : consumes) {
					reader = getRequestBodyReader(type);
					if (reader != null) {
						break;
					}
				}
			}
		}

		if (reader != null) {

			return getRequestBodyReaderInstance(reader);
		}

		// fall back to generic writer ...
		return new GenericBodyReader();
	}

	private static Class<? extends HttpRequestBodyReader> getRequestBodyReader(MediaType mediaType) {

		if (mediaType == null) {
			return null;
		}

		return MEDIA_TYPE_READERS.get(getMediaTypeKey(mediaType));
	}

	private static HttpRequestBodyReader getRequestBodyReaderInstance(Class<? extends HttpRequestBodyReader> reader) {

		if (reader != null) {
			try {
				// TODO .. might be a good idea to cache reader instances for some time
				return reader.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				log.error("Failed to instantiate request body reader '" + reader.getName() + "' " + e.getMessage(), e);
				// TODO: probably best to throw exception here
			}
		}

		return null;
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

		if (mediaType == null) {
			return MediaType.WILDCARD;
		}

		return mediaType.getType() + "/" + mediaType.getSubtype(); // remove charset if present when searching for
	}

	/**
	 * Finds assigned response writer or tries to assign a writer according to produces annotation and result type
	 *
	 * @param returnType type of result
	 * @param definition method definition
	 * @return writer to be used to produce response, {@see GenericResponseWriter} in case no suitable writer could be found
	 */
	private static HttpResponseWriter getResponseWriter(Class<?> returnType, RouteDefinition definition) {

		// 1. if route has a explicit writer defined ... then return this writer
		Class<? extends HttpResponseWriter> writer = definition.getWriter();

		// 2. if no writer is specified ... try to find appropriate writer by response type
		if (writer == null) {

			if (returnType == null) {
				writer = NoContentResponseWriter.class;
			}
			else {
				// try to find appropriate writer if mapped
				writer = CLASS_TYPE_WRITERS.get(returnType.getName());
			}
		}

		if (writer == null) { // try by produces

			MediaType[] produces = definition.getProduces();
			if (produces != null && produces.length > 0) {

				for (MediaType type : produces) {
					writer = getResponseWriter(getMediaTypeKey(type));
					if (writer != null) {
						break;
					}
				}
			}
		}

		if (writer != null) {

			return getResponseWriterInstance(writer);
		}

		// fall back to generic writer ...
		return new GenericResponseWriter();
	}

	private static Class<? extends HttpResponseWriter> getResponseWriter(String mediaType) {

		if (mediaType == null) {
			return null;
		}

		MediaType type = MediaType.valueOf(mediaType);
		return MEDIA_TYPE_WRITERS.get(getMediaTypeKey(type));
	}

	private static HttpResponseWriter getResponseWriterInstance(Class<? extends HttpResponseWriter> writer) {

		if (writer != null) {
			try {
				// TODO .. might be a good idea to cache writer instances for some time
				return writer.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				log.error("Failed to instantiate response writer '" + writer.getName() + "' " + e.getMessage(), e);
				// TODO: probably best to throw exception here
			}
		}

		return null;
	}

	public static HttpResponseWriter getResponseWriterInstance(String mediaType) {

		Class<? extends HttpResponseWriter> writer = getResponseWriter(mediaType);
		return getResponseWriterInstance(writer);
	}
}

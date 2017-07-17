package com.zandero.rest;

import com.zandero.rest.data.*;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.exception.ExceptionHandlerFactory;
import com.zandero.rest.exception.ExecuteException;
import com.zandero.rest.reader.HttpRequestBodyReader;
import com.zandero.rest.reader.ReaderFactory;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.rest.writer.WriterFactory;
import com.zandero.utils.Assert;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	private static final WriterFactory writers = new WriterFactory();

	private static final ReaderFactory readers = new ReaderFactory();

	private static final ExceptionHandlerFactory handlers = new ExceptionHandlerFactory();

	static Class<? extends ExceptionHandler> globalErrorHandlers[] = null;

	static Class<? extends HttpResponseWriter> globalErrorWriters[] = null;

	/**
	 * Searches for annotations to register routes ...
	 *
	 * @param vertx   Vert.X instance
	 * @param restApi instance to search for annotations
	 * @return Router new Router with routes as defined in {@code restApi} class
	 */
	public static Router register(Vertx vertx, Object... restApi) {

		Assert.notNull(vertx, "Missing vertx!");

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

		// TODO: split into smaller chucks

		Assert.notNull(router, "Missing vert.x router!");
		Assert.isTrue(restApi != null && restApi.length > 0, "Missing REST API class object!");

		for (Object api : restApi) {

			// check if api is an instance of a class or a class type
			if (api instanceof Class) {
				Class inspectApi = (Class) api;
				try {
					api = ClassFactory.newInstanceOf(inspectApi);
				} catch (ClassFactoryException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
			}

			Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(api.getClass());

			boolean bodyHandlerRegistered = false;
			boolean cookieHandlerRegistered = false;

			for (RouteDefinition definition : definitions.keySet()) {

				// add BodyHandler in case request has a body ...
				if (definition.requestHasBody() && !bodyHandlerRegistered) {
					router.route().handler(BodyHandler.create());
					bodyHandlerRegistered = true;
				}

				// add CookieHandler in case cookies are expected
				if (definition.hasCookies() && !cookieHandlerRegistered) {
					router.route().handler(CookieHandler.create());
					cookieHandlerRegistered = true;
				}

				Method method = definitions.get(definition);
				// add security check handler in front of regular route handler
				if (definition.checkSecurity()) {
					checkSecurity(router, definition, method);
				}

				// bind method execution
				Route route;
				if (definition.pathIsRegEx()) {
					route = router.routeWithRegex(definition.getMethod(), definition.getRoutePath());
				} else {
					route = router.route(definition.getMethod(), definition.getRoutePath());
				}

				log.info("Registering route: " + definition);

				if (definition.getConsumes() != null) {
					for (MediaType item : definition.getConsumes()) {
						route.consumes(MediaTypeHelper.getKey(item)); // ignore charset when binding
					}
				}

				if (definition.getProduces() != null) {
					for (MediaType item : definition.getProduces()) {
						route.produces(MediaTypeHelper.getKey(item)); // ignore charset when binding
					}
				}

				if (definition.getOrder() != 0) {
					route.order(definition.getOrder());
				}

				// check body and reader compatibility beforehand
				getBodyReader(definition);

				// check writer compatibility beforehand
				getWriter(method, definition);

				// bind handler
				Handler<RoutingContext> handler = getHandler(api, definition, method);
				if (definition.isBlocking()) {
					route.blockingHandler(handler);
				} else {
					route.handler(handler);
				}
			}
		}

		return router;
	}

	private static HttpRequestBodyReader getBodyReader(RouteDefinition definition) {

		if (!definition.requestHasBody() || !definition.hasBodyParameter()) {
			return null;
		}

		HttpRequestBodyReader bodyReader = readers.getRequestBodyReader(definition);

		if (bodyReader != null) {

			Type readerType = ClassFactory.getGenericType(bodyReader.getClass());
			MethodParameter bodyParameter = definition.getBodyParameter();

			ClassFactory.checkIfCompatibleTypes(bodyParameter.getDataType(), readerType, definition.toString().trim() + " - Parameter type: '" +
					                                                                             bodyParameter.getDataType() + "' not matching reader type: '" +
					                                                                             readerType + "' in: '" + bodyReader
					                                                                                                                                                                                                                   .getClass() + "'");
		}

		return bodyReader;
	}

	private static HttpResponseWriter getWriter(Method method, RouteDefinition definition) {

		HttpResponseWriter writer = writers.getResponseWriter(method.getReturnType(), definition);
		if (writer == null) {
			return null;
		}

		Type writerType = ClassFactory.getGenericType(writer.getClass());
		ClassFactory.checkIfCompatibleTypes(method.getReturnType(), writerType, definition.toString().trim() + " - Response type: '" +
				                                                                        method.getReturnType() + "' not matching writer type: '" +
				                                                                        writerType + "' in: '" + writer.getClass() + "'");

		return writer;
	}

	public static void errorHandler(Class<? extends ExceptionHandler>... exceptionHandlers) {

		Assert.notNullOrEmpty(exceptionHandlers, "Missing error handler(s)!");
		globalErrorHandlers = exceptionHandlers;
	}

	public static void errorWriter(Class<? extends HttpResponseWriter>... exceptionWriters) {

		Assert.notNullOrEmpty(exceptionWriters, "Missing error writer(s)!");
		globalErrorWriters = exceptionWriters;
	}

	private static void checkSecurity(Router router, final RouteDefinition definition, final Method method) {

		Route route;
		if (definition.pathIsRegEx()) {
			route = router.routeWithRegex(definition.getMethod(), definition.getRoutePath());
		} else {
			route = router.route(definition.getMethod(), definition.getRoutePath());
		}

		route.order(definition.getOrder()); // same order as following handler

		Handler<RoutingContext> securityHandler = getSecurityHandler(definition, method);
		if (definition.isBlocking()) {
			route.blockingHandler(securityHandler);
		} else {
			route.handler(securityHandler);
		}
	}

	private static Handler<RoutingContext> getSecurityHandler(final RouteDefinition definition, final Method method) {

		return context -> {

			boolean allowed = isAllowed(context.user(), definition);
			if (allowed) {
				context.next();
			} else {
				handleException(new NotAuthorizedException("Not authorized to access: " + definition), context, definition);
			}
		};
	}

	private static boolean isAllowed(User user, RouteDefinition definition) {

		if (definition.getPermitAll() != null) {
			// allow all or deny all
			return definition.getPermitAll();
		}

		if (user == null) {
			return false; // no user present ... can't check
		}

		// check if given user is authorized for given role ...
		List<Future> list = new ArrayList<>();

		for (String role : definition.getRoles()) {

			Future<Boolean> future = Future.future();
			user.isAuthorised(role, future.completer());

			list.add(future);
		}

		// compose multiple futures ... and return true if any of those return true
		Future<CompositeFuture> output = Future.future();
		CompositeFuture.all(list).setHandler(output.completer());

		if (output.result() != null) {

			for (int index = 0; index < output.result().size(); index++) {
				if (output.result().succeeded(index)) {

					Object result = output.result().resultAt(index);
					if (result instanceof Boolean && ((Boolean) result))
						return true;
				}
			}
		}

		return false;
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {

				HttpResponseWriter writer = getWriter(method, definition);
				HttpRequestBodyReader reader = getBodyReader(definition);

				Object[] args = ArgumentProvider.getArguments(method, definition, context, reader);

				Object result = method.invoke(toInvoke, args);

				produceResponse(result, context, definition, writer);

			} catch (Exception e) {

				handleException(e, context, definition);
			}
		};
	}

	private static void handleException(Exception e, RoutingContext context, final RouteDefinition definition) {

		ExecuteException ex = getExecuteException(e);

		// get appropriate writer ...
		HttpResponseWriter writer = writers.getFailureWriter(definition.getFailureWriters(globalErrorWriters),
		                                                     ex.getCause().getClass(),
		                                                     definition);

		HttpServerResponse response = context.response();
		response.setStatusCode(ex.getStatusCode());
		writer.addResponseHeaders(definition, response);

		// fill up as much as we can ... default behavior
		// get default handler by exception type or use global error handler ...

		// route through handler ... to allow customization
		ExceptionHandler handler = handlers.getFailureHandler(definition.getFailureHandlers(globalErrorHandlers),
		                                                      ex.getCause().getClass());
		handler.handle(ex.getCause(), writer, context);

		// end response ...
		if (!response.ended()) {
			response.end();
		}
	}

	private static ExecuteException getExecuteException(Throwable e) {

		// unwrap invoke exception ...
		if (e instanceof IllegalAccessException || e instanceof InvocationTargetException) {

			if (e.getCause() != null) {
				return getExecuteException(e.getCause());
			}
		}

		if (e instanceof IllegalArgumentException) {
			return new ExecuteException(400, e);
		}

		return new ExecuteException(500, e);
	}

	private static void produceResponse(Object result, RoutingContext context, RouteDefinition definition, HttpResponseWriter writer) {

		HttpServerResponse response = context.response();
		HttpServerRequest request = context.request();

		// add default response headers per definition
		writer.addResponseHeaders(definition, response);

		// write response and override headers if necessary
		writer.write(result, request, response);

		// finish if not finished by writer
		if (!response.ended()) {
			response.end();
		}
	}

	public static WriterFactory getWriters() {

		return writers;
	}

	public static ReaderFactory getReaders() {

		return readers;
	}

	static void pushContext(RoutingContext context, Object object) {

		Assert.notNull(context, "Missing context!");
		Assert.notNull(object, "Can't push null into context!");

		context.put(ArgumentProvider.getContextKey(object), object);
	}
}

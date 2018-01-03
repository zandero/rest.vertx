package com.zandero.rest;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.context.ContextProviders;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.injection.InjectorFactory;
import com.zandero.rest.reader.ReaderFactory;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.rest.writer.NotFoundResponseWriter;
import com.zandero.rest.writer.WriterFactory;
import com.zandero.utils.Assert;
import com.zandero.utils.extra.ValidatingUtils;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	private static final WriterFactory writers = new WriterFactory();

	private static final ReaderFactory readers = new ReaderFactory();

	private static final ExceptionHandlerFactory handlers = new ExceptionHandlerFactory();

	private static final ContextProviders providers = new ContextProviders();

	private static InjectionProvider injectionProvider;

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

		Assert.notNull(router, "Missing vertx router!");
		Assert.isTrue(restApi != null && restApi.length > 0, "Missing REST API class object!");
		assert restApi != null;

		for (Object api : restApi) {

			// check if api is an instance of a class or a class type
			if (api instanceof Class) {

				Class inspectApi = (Class) api;

				try {
					api = ClassFactory.newInstanceOf(injectionProvider, inspectApi);
				}
				catch (ClassFactoryException e) {
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

				if (definition.requestHasBody() && definition.getConsumes() != null) { // only register if request with body
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
				checkBodyReader(definition);

				// check writer compatibility beforehand
				getWriter(method, definition, null); // no way to know the accept content at this point

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

	/**
	 * Handles not found route for all requests
	 *
	 * @param router   to add route to
	 * @param notFound hander
	 */
	public static void notFound(Router router, Class<? extends NotFoundResponseWriter> notFound) {
		notFound(router, null, notFound);
	}

	/**
	 * Handles not found route in case request path mathes given path prefix
	 *
	 * @param router   to add route to
	 * @param path     prefix
	 * @param notFound hander
	 */
	public static void notFound(Router router, String path, Class<? extends NotFoundResponseWriter> notFound) {

		Assert.notNull(router, "Missing router!");
		Assert.notNull(notFound, "Missing not found handler!");

		if (path == null) {
			router.route().last().handler(getNotFoundHandler(notFound));
		} else {

			if (!ValidatingUtils.isRegEx(path)) {

				if (!path.startsWith("/")) {
					path = "/" + path;
				}

				if (!path.endsWith("/")) {
					path = path + "/";
				}

				path = path.replaceAll("\\/", "\\\\/");  // escape path to be valid regEx
				path = path + ".*";
			}

			router.routeWithRegex(path).last().handler(getNotFoundHandler(notFound));
		}
	}

	/**
	 * @param router               to add handler to
	 * @param allowedOriginPattern origin pattern
	 * @param allowCredentials     allowed credentials
	 * @param maxAge               in seconds
	 * @param allowedHeaders       set of headers or null for none
	 * @param methods              list of methods or empty for all
	 */
	public void enableCors(Router router,
	                       String allowedOriginPattern,
	                       boolean allowCredentials,
	                       int maxAge,
	                       Set<String> allowedHeaders,
	                       HttpMethod... methods) {

		CorsHandler handler = CorsHandler.create(allowedOriginPattern)
		                                 .allowCredentials(allowCredentials)
		                                 .maxAgeSeconds(maxAge);

		if (methods == null || methods.length == 0) { // if not given than all
			methods = HttpMethod.values();
		}

		for (HttpMethod method : methods) {
			handler.allowedMethod(method);
		}

		handler.allowedHeaders(allowedHeaders);

		router.route().handler(handler);
	}

	private static void checkBodyReader(RouteDefinition definition) {

		if (!definition.requestHasBody() || !definition.hasBodyParameter()) {
			return;
		}

		ValueReader bodyReader = readers.get(injectionProvider, definition.getBodyParameter(), definition.getReader(), definition.getConsumes());

		if (bodyReader != null) {

			Type readerType = ClassFactory.getGenericType(bodyReader.getClass());
			MethodParameter bodyParameter = definition.getBodyParameter();

			ClassFactory.checkIfCompatibleTypes(bodyParameter.getDataType(), readerType,
			                                    definition.toString().trim() + " - Parameter type: '" +
			                                    bodyParameter.getDataType() + "' not matching reader type: '" +
			                                    readerType + "' in: '" + bodyReader.getClass() + "'");
		}
	}

	private static HttpResponseWriter getWriter(Method method, RouteDefinition definition, MediaType acceptHeader) {

		HttpResponseWriter writer = writers.getResponseWriter(injectionProvider, method.getReturnType(), definition, acceptHeader);
		if (writer == null) {
			return null;
		}

		Type writerType = ClassFactory.getGenericType(writer.getClass());
		ClassFactory.checkIfCompatibleTypes(method.getReturnType(), writerType, definition.toString().trim() + " - Response type: '" +
		                                                                        method.getReturnType() + "' not matching writer type: '" +
		                                                                        writerType + "' in: '" + writer.getClass() + "'");

		return writer;
	}

	private static void checkSecurity(Router router, final RouteDefinition definition, final Method method) {

		Route route;
		if (definition.pathIsRegEx()) {
			route = router.routeWithRegex(definition.getMethod(), definition.getRoutePath());
		} else {
			route = router.route(definition.getMethod(), definition.getRoutePath());
		}

		route.order(definition.getOrder()); // same order as following handler

		// TODO: add security handler the same way as Context handlers are added
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
				handleException(new ExecuteException(Response.Status.UNAUTHORIZED.getStatusCode(), "HTTP 401 Unauthorized"), context, definition);
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
					if (result instanceof Boolean && ((Boolean) result)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {
				MediaType accept = MediaTypeHelper.valueOf(context.getAcceptableContentType());
				HttpResponseWriter writer = getWriter(method, definition, accept);

				Object[] args = ArgumentProvider.getArguments(method, definition, context, readers, providers, injectionProvider);

				Object result = method.invoke(toInvoke, args);

				produceResponse(result, context, definition, writer);
			}
			catch (Exception e) {
				handleException(e, context, definition);
			}
		};
	}

	private static Handler<RoutingContext> getNotFoundHandler(Class<? extends NotFoundResponseWriter> notFoundWriter) {
		return context -> {

			try {
				// fill up definition (response headers) from request
				RouteDefinition definition = new RouteDefinition(context);
				produceResponse(null, context, definition, notFoundWriter.newInstance());
			}
			catch (Exception e) {
				handleException(e, context, null);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private static void handleException(Exception e, RoutingContext context, final RouteDefinition definition) {

		ExecuteException ex = getExecuteException(e);

		// get appropriate exception handler/writer ...
		ExceptionHandler handler;
		try {
			Class<? extends Throwable> clazz;
			if (ex.getCause() == null) {
				clazz = ex.getClass();
			} else {
				clazz = ex.getCause().getClass();
			}

			handler = handlers.getExceptionHandler(definition.getExceptionHandlers(), clazz);
		}
		catch (ClassFactoryException classException) {
			// Can't provide exception handler ... rethrow
			log.error("Can't provide exception handler!", classException);
			// fall back to generic ...
			handler = new GenericExceptionHandler();
			ex = new ExecuteException(500, classException);
		}

		HttpServerResponse response = context.response();
		response.setStatusCode(ex.getStatusCode());
		handler.addResponseHeaders(definition, response);

		handler.write(ex.getCause(), context.request(), context.response());

		// end response ...
		if (!response.ended()) {
			response.end();
		}
	}

	private static ExecuteException getExecuteException(Throwable e) {

		if (e instanceof ExecuteException) {
			ExecuteException ex = (ExecuteException) e;
			return new ExecuteException(ex.getStatusCode(), ex.getMessage(), ex);
		}

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

	@SuppressWarnings("unchecked")
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

	public static ExceptionHandlerFactory getExceptionHandlers() {

		return handlers;
	}

	/**
	 * Registers a context provider for given type of class
	 *
	 * @param aClass   to register context provider for
	 * @param provider to be registered
	 * @param <T>      provider type
	 */
	public static <T> void addContextProvider(Class<T> aClass, ContextProvider<T> provider) {

		providers.register(aClass, provider);
	}

	public static ContextProviders getContextProviders() {
		return providers;
	}

	static void pushContext(RoutingContext context, Object object) {

		Assert.notNull(context, "Missing context!");
		Assert.notNull(object, "Can't push null into context!");

		context.put(ArgumentProvider.getContextKey(object), object);
	}

	/**
	 * Provide an injector to inject classes where needed
	 * @param provider to inject classes
	 */
	public static void injectWith(InjectionProvider provider) {

		injectionProvider = provider;
	}

	/**
	 * Provide an injector to inject classes where needed
	 * @param provider to create to inject classes
	 */
	public static void injectWith(Class<InjectionProvider> provider) {

		try {
			injectionProvider = (InjectionProvider) InjectorFactory.newInstanceOf(provider);
		}
		catch (ClassFactoryException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

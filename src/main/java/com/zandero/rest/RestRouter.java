package com.zandero.rest;

import com.zandero.rest.annotation.Event;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.context.ContextProviderFactory;
import com.zandero.rest.data.*;
import com.zandero.rest.events.RestEvent;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.reader.ReaderFactory;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.GenericResponseWriter;
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
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	public static final int ORDER_CORS_HANDLER = -10;
	public static final int ORDER_PROVIDER_HANDLER = -5;

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	private static final WriterFactory writers = new WriterFactory();

	private static final ReaderFactory readers = new ReaderFactory();

	private static final ExceptionHandlerFactory handlers = new ExceptionHandlerFactory();

	private static final ContextProviderFactory providers = new ContextProviderFactory();

	private static InjectionProvider injectionProvider;
	private static Validator validator;

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

		Assert.notNull(router, "Missing vertx router!");
		Assert.isTrue(restApi != null && restApi.length > 0, "Missing REST API class object!");
		assert restApi != null;

		for (Object api : restApi) {

			if (api == null) {
				continue;
			}

			if (api instanceof Handler) {

				Handler handler = (Handler)api;
				router.route().handler(handler);
				continue;
			}

			// check if api is an instance of a class or a class type
			if (api instanceof Class) {

				Class inspectApi = (Class) api;

				try {
					api = ClassFactory.newInstanceOf(inspectApi, injectionProvider, null);
				}
				catch (ClassFactoryException | ContextException e) {
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

				// add security check handler in front of regular route handler
				if (definition.checkSecurity()) {
					checkSecurity(router, definition);
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

				Method method = definitions.get(definition);

				// bind handler // blocking or async
				Handler<RoutingContext> handler;

				if (definition.isAsync()) {
					handler = getAsyncHandler(api, definition, method);
				} else {
					checkWriterCompatibility(definition);
					handler = getHandler(api, definition, method);
				}

				route.handler(handler);
			}
		}

		return router;
	}

	// Check writer compatibility if possible
	private static void checkWriterCompatibility(RouteDefinition definition) {
		try { // no way to know the accept content at this point
			getWriter(injectionProvider, definition.getReturnType(), definition, null, null, GenericResponseWriter.class);
		}
		catch (ClassFactoryException e) {
			// not relevant at this point
		}
	}

	public static void provide(Router output, Class<? extends ContextProvider> provider) {

		try {
			Class clazz = (Class) ClassFactory.getGenericType(provider);
			ContextProvider instance = getContextProviders().getContextProvider(injectionProvider,
			                                                                    clazz,
			                                                                    provider,
			                                                                    null);
			// set before other routes ...
			output.route().order(ORDER_PROVIDER_HANDLER).blockingHandler(getContextHandler(instance));
		}
		catch (ClassFactoryException | ContextException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static void provide(Router output, ContextProvider<?> provider) {

		output.route().blockingHandler(getContextHandler(provider));
	}

	private static Handler<RoutingContext> getContextHandler(ContextProvider instance) {

		return context -> {

			if (instance != null) {
				Object provided = instance.provide(context.request());

				if (provided instanceof User) {
					context.setUser((User) provided);
				}

				if (provided instanceof Session) {
					context.setSession((Session) provided);
				}

				if (provided != null) {
					context.data().put(ContextProviderFactory.getContextKey(provided), provided);
				}
			}

			context.next();
		};
	}

	@SuppressWarnings("unchecked")
	public static void handler(Router output, Class<? extends Handler<RoutingContext>> handler) {

		try {
			Handler<RoutingContext> instance = (Handler<RoutingContext>) ClassFactory.newInstanceOf(handler, injectionProvider, null);
			output.route().handler(instance);
		}
		catch (ClassFactoryException | ContextException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Handles not found route for all requests
	 *
	 * @param router   to add route to
	 * @param notFound handler
	 */
	public static void notFound(Router router, Class<? extends NotFoundResponseWriter> notFound) {
		notFound(router, null, notFound);
	}

	/**
	 * Handles not found route for all requests
	 *
	 * @param router   to add route to
	 * @param notFound handler
	 */
	public static void notFound(Router router, NotFoundResponseWriter notFound) {
		notFound(router, null, notFound);
	}

	/**
	 * Handles not found route in case request path mathes given path prefix
	 *
	 * @param router   to add route to
	 * @param path     prefix
	 * @param notFound handler
	 */
	public static void notFound(Router router, String path, NotFoundResponseWriter notFound) {

		Assert.notNull(router, "Missing router!");
		Assert.notNull(notFound, "Missing not found handler!");

		addLastHandler(router, path, getNotFoundHandler(notFound));
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

		addLastHandler(router, path, getNotFoundHandler(notFound));
	}

	private static void addLastHandler(Router router, String path, Handler<RoutingContext> notFoundHandler) {
		if (path == null) {
			router.route().last().handler(notFoundHandler);
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

			router.routeWithRegex(path).last().handler(notFoundHandler);
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
		router.route().handler(handler).order(ORDER_CORS_HANDLER);
	}

	private static void checkBodyReader(RouteDefinition definition) {

		if (!definition.requestHasBody() || !definition.hasBodyParameter()) {
			return;
		}

		ValueReader bodyReader = readers.get(definition.getBodyParameter(), definition.getReader(), injectionProvider,
		                                     null,
		                                     definition.getConsumes());

		if (bodyReader != null && definition.checkCompatibility()) {

			Type readerType = ClassFactory.getGenericType(bodyReader.getClass());
			MethodParameter bodyParameter = definition.getBodyParameter();

			ClassFactory.checkIfCompatibleTypes(bodyParameter.getDataType(), readerType,
			                                    definition.toString().trim() + " - Parameter type: '" +
			                                    bodyParameter.getDataType() + "' not matching reader type: '" +
			                                    readerType + "' in: '" + bodyReader.getClass() + "'!");
		}
	}

	private static HttpResponseWriter getWriter(InjectionProvider injectionProvider,
	                                            Class returnType,
	                                            RouteDefinition definition,
	                                            MediaType acceptHeader,
	                                            RoutingContext context,
	                                            Class<? extends HttpResponseWriter> defaultTo) throws ClassFactoryException {

		if (returnType == null) {
			returnType = definition.getReturnType();
		}

		HttpResponseWriter writer = writers.getResponseWriter(returnType, definition, injectionProvider, context, acceptHeader);

		if (writer == null) {
			log.error("No writer could be provided. Falling back to " + defaultTo.getSimpleName() + " instead!");
			return (HttpResponseWriter) ClassFactory.newInstanceOf(defaultTo);
		}

		if (definition.checkCompatibility() &&
		    ClassFactory.checkCompatibility(writer.getClass())) {

			Type writerType = ClassFactory.getGenericType(writer.getClass());
			ClassFactory.checkIfCompatibleTypes(returnType,
			                                    writerType,
			                                    definition.toString().trim() + " - Response type: '" +
			                                    returnType + "' not matching writer type: '" +
			                                    writerType + "' in: '" + writer.getClass() + "'!");
		}

		return writer;
	}

	private static void checkSecurity(Router router, final RouteDefinition definition) {

		Route route;
		if (definition.pathIsRegEx()) {
			route = router.routeWithRegex(definition.getMethod(), definition.getRoutePath());
		} else {
			route = router.route(definition.getMethod(), definition.getRoutePath());
		}

		route.order(definition.getOrder()); // same order as following handler

		Handler<RoutingContext> securityHandler = getSecurityHandler(definition);
		route.blockingHandler(securityHandler);
	}

	private static Handler<RoutingContext> getSecurityHandler(final RouteDefinition definition) {

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
			user.isAuthorized(role, future.completer());

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

		return context -> context.vertx().executeBlocking(
			fut -> {
				try {
					Object[] args = ArgumentProvider.getArguments(method, definition, context, readers, providers, injectionProvider);
					validate(method, definition, validator, toInvoke, args);

					fut.complete(method.invoke(toInvoke, args));
				}
				catch (Exception e) {
					fut.fail(e);
				}
			},
			definition.executeBlockingOrdered(), // false by default
			res -> {

				if (res.succeeded()) {
					try {

						Object result = res.result();
						Class returnType = result != null ? result.getClass() : definition.getReturnType();

						MediaType accept = MediaTypeHelper.valueOf(context.getAcceptableContentType());
						HttpResponseWriter writer = getWriter(injectionProvider,
						                                      returnType,
						                                      definition,
						                                      accept,
						                                      context,
						                                      GenericResponseWriter.class);


						produceResponse(res.result(), context, definition, writer);
					}
					catch (Throwable e) {
						handleException(e, context, definition);
					}
				} else {
					handleException(res.cause(), context, definition);
				}
			}
		);
	}

	private static void validate(Method method, RouteDefinition definition, Validator validator, Object toInvoke, Object[] args) {

		if (validator != null) {

			// check method params first
			ExecutableValidator executableValidator = validator.forExecutables();
			Set<ConstraintViolation<Object>> result = executableValidator.validateParameters(toInvoke, method, args);
			if (result != null && result.size() > 0) {
				throw new ConstraintException(definition, result);
			}
		}
	}

	private static Handler<RoutingContext> getAsyncHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

		return context -> {

			try {
				Object[] args = ArgumentProvider.getArguments(method, definition, context, readers, providers, injectionProvider);
				validate(method, definition, validator, toInvoke, args);

				Object result = method.invoke(toInvoke, args);

				if (result instanceof Future) {
					Future<?> fut = (Future) result;

					// wait for future to complete ... don't block vertx event bus in the mean time
					fut.setHandler(handler -> {

						if (fut.succeeded()) {

							try {
								Object futureResult = fut.result();
								MediaType accept = MediaTypeHelper.valueOf(context.getAcceptableContentType());

								HttpResponseWriter writer;
								if (futureResult != null) { // get writer from result type otherwise we don't know
									writer = getWriter(injectionProvider,
									                   futureResult.getClass(),
									                   definition,
									                   accept,
									                   context,
									                   GenericResponseWriter.class);
								} else { // due to limitations of Java generics we can't tell the type if response is null
									Class<?> writerClass = definition.getWriter() == null ? GenericResponseWriter.class : definition.getWriter();
									writer = (HttpResponseWriter) WriterFactory.newInstanceOf(writerClass);
								}

								produceResponse(futureResult, context, definition, writer);
							}
							catch (Throwable e) {
								handleException(e, context, definition);
							}
						} else {
							handleException(fut.cause(), context, definition);
						}
					});
				}
			}
			catch (Exception e) {
				handleException(e, context, definition);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private static Handler<RoutingContext> getNotFoundHandler(Object notFoundWriter) {
		return context -> {

			try {
				// fill up definition (response headers) from request
				RouteDefinition definition = new RouteDefinition(context);

				HttpResponseWriter writer;
				if (notFoundWriter instanceof Class) {
					writer = writers.getClassInstance((Class<? extends HttpResponseWriter>) notFoundWriter, injectionProvider, context);
				} else {
					writer = (HttpResponseWriter) notFoundWriter;
				}

				produceResponse(null, context, definition, writer);
			}
			catch (Throwable e) {
				handleException(e, context, null);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private static void handleException(Throwable e, RoutingContext context, final RouteDefinition definition) {

		log.error("Handling exception: ", e);
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

			Class<? extends ExceptionHandler>[] exHandlers = null;
			if (definition != null) {
				exHandlers = definition.getExceptionHandlers();
			}

			handler = handlers.getExceptionHandler(clazz, exHandlers, injectionProvider, context);
		}
		catch (ClassFactoryException classException) {
			// Can't provide exception handler ... rethrow
			log.error("Can't provide exception handler!", classException);
			// fall back to generic ...
			handler = new GenericExceptionHandler();
			ex = new ExecuteException(500, classException);
		}
		catch (ContextException contextException) {
			// Can't provide @Context for handler ... rethrow
			log.error("Can't provide @Context!", contextException);
			// fall back to generic ...
			handler = new GenericExceptionHandler();
			ex = new ExecuteException(500, contextException);
		}

		HttpServerResponse response = context.response();
		response.setStatusCode(ex.getStatusCode());
		handler.addResponseHeaders(definition, response);

		try {
			handler.write(ex.getCause(), context.request(), context.response());
		}
		catch (Throwable handlerException) {
			// this should not happen
			log.error("Failed to write out handled exception: " + e.getMessage(), e);
		}

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
	private static void produceResponse(Object result, RoutingContext context, RouteDefinition definition, HttpResponseWriter writer) throws
	                                                                                                                                  Throwable {
		HttpServerResponse response = context.response();
		HttpServerRequest request = context.request();

		// add default response headers per definition (or from writer definition)
		writer.addResponseHeaders(definition, response);

		// write response and override headers if necessary
		writer.write(result, request, response);

		// find and trigger events from // result / response
		triggerEvents(definition, context, result, response.getStatusCode());

		// finish if not finished by writer
		// and is not an Async REST (Async RESTs must finish responses on their own)
		if (!definition.isAsync() &&
		    !response.ended()) {
			response.end();
		}
	}


	/**
	 * Matches events to result / response
	 * @param result of method
	 * @param responseCode produced by writer
	 * @return list of matching events or empty list if none found
	 */
	private static List<RestEvent> getEvents(RouteDefinition definition, RoutingContext context, Object result, int responseCode) {

		if (definition.getEvents() == null) {
			return Collections.emptyList();
		}

		List<RestEvent> matching = new ArrayList<>();
		for (Event event: definition.getEvents()) {
			// status code match OR all
			if (event.response() == Event.DEFAULT_EVENT_STATUS || event.response() == responseCode) {

				Class<? extends RestEvent> processor = event.value();

				// check if generics fit
				if (result != null) {
					Type type = ClassFactory.getGenericType(processor);

					if (ClassFactory.checkIfCompatibleTypes(result.getClass(), type) ) {
						try {
							// TODO: ... trigger directly via event bus ...

							RestEvent instance = (RestEvent) ClassFactory.newInstanceOf(processor, injectionProvider, context);
							matching.add(instance);
						}
						catch (ClassFactoryException | ContextException e) {
							// TODO should we throw or swallow?
							log.error("Failed to provide RestEvent for: " + definition + " ", e);
						}
					}
				}
			}
		}

		return matching;
	}

	private static void triggerEvents(RouteDefinition definition, RoutingContext context, Object result, int responseCode) throws Throwable {
		List<RestEvent> found = getEvents(definition, context, result, responseCode);
		if (found.size() > 0) {

			for (RestEvent event: found) {
				event.execute(result, context.vertx().eventBus());
			}
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
	 * @param provider clazz type to be registered
	 */
	public static void addProvider(Class<? extends ContextProvider> provider) {

		Class clazz = (Class) ClassFactory.getGenericType(provider);
		providers.register(clazz, provider);
	}

	public static <T> void addProvider(Class<T> clazz, ContextProvider<T> provider) {
		providers.register(clazz, provider);
	}

	public static ContextProviderFactory getContextProviders() {
		return providers;
	}

	static void pushContext(RoutingContext context, Object object) {

		Assert.notNull(context, "Missing context!");
		Assert.notNull(object, "Can't push null into context!");

		context.put(ContextProviderFactory.getContextKey(object), object);
	}

	/**
	 * Provide an injector to getInstance classes where needed
	 *
	 * @param provider to create/inject classes
	 */
	public static void injectWith(InjectionProvider provider) {

		injectionProvider = provider;
	}

	/**
	 * Provide an injector to getInstance classes where needed
	 *
	 * @param provider class type
	 */
	public static void injectWith(Class<InjectionProvider> provider) {

		try {
			injectionProvider = (InjectionProvider) ClassFactory.newInstanceOf(provider);
		}
		catch (ClassFactoryException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Provide an validator to validate arguments
	 *
	 * @param provider to validate
	 */
	public static void validateWith(Validator provider) {

		validator = provider;
	}

	/**
	 * Provide an validator to validate arguments
	 *
	 * @param provider class type
	 */
	public static void validateWith(Class<Validator> provider) {

		try {
			validator = (Validator) ClassFactory.newInstanceOf(provider);
		}
		catch (ClassFactoryException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

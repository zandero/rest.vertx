package com.zandero.rest;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.rest.writer.NotFoundResponseWriter;
import com.zandero.utils.Assert;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Helper class to build up RestRouter with all writers, readers, handlers and context providers in one place
 */
public class RestBuilder {

	private final Vertx vertx;

	private final Router router;

	private List<Object> apis = new ArrayList<>();

	private List<Object> contextProviders = new ArrayList<>();
	private Map<Class, Object> registeredProviders = new HashMap<>();

	private List<Object> exceptionHandlers = new ArrayList<>();

	private Map<MediaType, Object> mediaTypeResponseWriters = new LinkedHashMap<>();
	private Map<Class, Object> classResponseWriters = new LinkedHashMap<>();

	private Map<MediaType, Object> mediaTypeValueReaders = new LinkedHashMap<>();
	private Map<Class, Object> classValueReaders = new LinkedHashMap<>();

	/**
	 * Map of path / not found handlers
	 */
	private Map<String, Object> notFound = new LinkedHashMap<>();

	/**
	 * CORS handler if desired
	 */
	private CorsHandler corsHandler = null;

	private InjectionProvider injectionProvider = null;

	public RestBuilder(Router router) {

		Assert.notNull(router, "Missing vertx router!");

		this.router = router;
		this.vertx = null;
	}

	public RestBuilder(Vertx vertx) { // hide

		Assert.notNull(vertx, "Missing vertx!");

		this.router = null;
		this.vertx = vertx;
	}

	public RestBuilder register(Object... restApi) {

		Assert.notNullOrEmpty(restApi, "Missing REST API(s)!");

		apis.addAll(Arrays.asList(restApi));
		return this;
	}

	public RestBuilder notFound(String path, Class<? extends NotFoundResponseWriter> writer) {

		Assert.notNullOrEmptyTrimmed(path, "Missing path prefix!");
		Assert.notNull(writer, "Missing not fount response writer!");

		notFound.put(path, writer); // adds route path prefix
		return this;
	}

	public RestBuilder notFound(Class<? extends NotFoundResponseWriter> writer) {

		Assert.notNull(writer, "Missing not fount response writer!");

		notFound.put(null, writer); // default ... handles all
		return this;
	}

	/**
	 * Enables CORS
	 *
	 * @param allowedOriginPattern allowed origin
	 * @param allowCredentials     allow credentials (true/false)
	 * @param maxAge               in seconds
	 * @param allowedHeaders       set of allowed headers
	 * @param methods              list of methods ... if empty all methods are allowed  @return self
	 * @return self
	 */
	public RestBuilder enableCors(String allowedOriginPattern,
	                              boolean allowCredentials,
	                              int maxAge,
	                              Set<String> allowedHeaders,
	                              HttpMethod... methods) {

		corsHandler = CorsHandler.create(allowedOriginPattern)
		                         .allowCredentials(allowCredentials)
		                         .maxAgeSeconds(maxAge);

		if (methods == null || methods.length == 0) { // if not given than all
			methods = HttpMethod.values();
		}

		for (HttpMethod method : methods) {
			corsHandler.allowedMethod(method);
		}

		corsHandler.allowedHeaders(allowedHeaders);

		return this;
	}

	/**
	 * Registeres one or more exception handler classes
	 * @param handlers to be registered
	 * @return builder
	 */
	@SafeVarargs
	public final RestBuilder errorHandler(Class<? extends ExceptionHandler>... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");

		exceptionHandlers.addAll(Arrays.asList(handlers));
		return this;
	}

	/**
	 * Registeres one or more exception handler instances
	 * @param handlers to be registered
	 * @return builder
	 */
	public RestBuilder errorHandler(ExceptionHandler<?>... handlers) {
		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");

		exceptionHandlers.addAll(Arrays.asList(handlers));
		return this;
	}

	public RestBuilder writer(Class<?> clazz, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(clazz, "Missing response class!");
		Assert.notNull(writer, "Missing response writer type class!");

		classResponseWriters.put(clazz, writer);
		return this;
	}

	public RestBuilder writer(String mediaType, Class<? extends HttpResponseWriter> writer) {

		Assert.notNullOrEmptyTrimmed(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer class!");

		MediaType type = MediaTypeHelper.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		mediaTypeResponseWriters.put(type, writer);
		return this;
	}

	public RestBuilder writer(MediaType mediaType, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer class!");

		mediaTypeResponseWriters.put(mediaType, writer);
		return this;
	}

	public RestBuilder reader(Class<?> clazz, Class<? extends ValueReader> reader) {

		Assert.notNull(clazz, "Missing read in class!");
		Assert.notNull(reader, "Missing request reader type class!");

		classValueReaders.put(clazz, reader);
		return this;
	}

	public RestBuilder reader(String mediaType, Class<? extends ValueReader> reader) {

		Assert.notNullOrEmptyTrimmed(mediaType, "Missing media type!");
		Assert.notNull(reader, "Missing value reader class!");

		MediaType type = MediaTypeHelper.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		mediaTypeValueReaders.put(type, reader);
		return this;
	}

	public RestBuilder reader(MediaType mediaType, Class<? extends ValueReader> reader) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(reader, "Missing value reader class!");

		mediaTypeValueReaders.put(mediaType, reader);
		return this;
	}

	public <T> RestBuilder provide(ContextProvider<T> provider) {

		Assert.notNull(provider, "Missing context provider!");

		contextProviders.add(provider);
		return this;
	}

	public <T> RestBuilder provide(Class<? extends ContextProvider<T>> provider) {

		Assert.notNull(provider, "Missing context provider!");

		contextProviders.add(provider);
		return this;
	}

	public <T> RestBuilder addProvider(Class<T> clazz, ContextProvider<T> provider) {

		Assert.notNull(clazz, "Missing provided class type!");
		Assert.notNull(provider, "Missing context provider!");

		registeredProviders.put(clazz, provider);
		return this;
	}

	public <T> RestBuilder addProvider(Class<? extends ContextProvider<T>> provider) {

		Assert.notNull(provider, "Missing context provider!");

		Class clazz = (Class) ClassFactory.getGenericType(provider);
		registeredProviders.put(clazz, provider);
		return this;
	}

	/**
	 * Assosicate provider to getInstance members into REST classes, Reader, Writers ...
	 *
	 * @param provider to do the injection
	 * @return rest builder
	 */
	public RestBuilder injectWith(InjectionProvider provider) {
		injectionProvider = provider;
		return this;
	}

	public RestBuilder injectWith(Class<? extends InjectionProvider> provider) {
		try {
			injectionProvider = (InjectionProvider) ClassFactory.newInstanceOf(provider);
		}
		catch (ClassFactoryException e) {
			throw new IllegalArgumentException(e);
		}

		return this;
	}

	private Router getRouter() {
		if (vertx == null) {
			return RestRouter.register(router, apis);
		}

		return RestRouter.register(vertx, apis);
	}

	@SuppressWarnings("unchecked")
	public Router build() {

		Assert.notNullOrEmpty(apis, "No REST API given, register at least one! Use: .register(api) call!");

		Router output = getRouter();

		RestRouter.injectWith(injectionProvider);

		if (contextProviders.size() > 0) {

			contextProviders.forEach(provider -> {

				if (provider instanceof Class) {
					RestRouter.provide(output, (Class<? extends ContextProvider>) provider);
				} else {
					RestRouter.provide(output, (ContextProvider<?>) provider);
				}
			});
		}

		if (registeredProviders.size() > 0) {

			registeredProviders.forEach((clazz, provider) -> {

				if (provider instanceof Class) {
					RestRouter.addProvider((Class<? extends ContextProvider>) provider);
				} else {
					RestRouter.addProvider(clazz, (ContextProvider) provider);
				}
			});
		}

		// register APIs
		apis.forEach(api -> RestRouter.register(output, api));

		// register readers
		if (classValueReaders.size() > 0) {
			classValueReaders.forEach((clazz, reader) -> {

				if (reader instanceof Class) {
					RestRouter.getReaders().register(clazz, (Class<? extends ValueReader>) reader);
				} else {
					RestRouter.getReaders().register(clazz, (ValueReader) reader);
				}
			});
		}

		if (mediaTypeValueReaders.size() > 0) {
			mediaTypeValueReaders.forEach((type, reader) -> {
				if (reader instanceof Class) {
					RestRouter.getReaders().register(type, (Class<? extends ValueReader>) reader);
				} else {
					RestRouter.getReaders().register(type, (ValueReader) reader);
				}
			});
		}

		// register writers
		if (classResponseWriters.size() > 0) {
			classResponseWriters.forEach((clazz, writer) -> {
				if (writer instanceof Class) {
					RestRouter.getWriters().register(clazz, (Class<? extends HttpResponseWriter>) writer);
				}
				else {
					RestRouter.getWriters().register(clazz, (HttpResponseWriter) writer);
				}
			});
		}

		if (mediaTypeResponseWriters.size() > 0) {
			mediaTypeResponseWriters.forEach((type, writer) -> {
				if (writer instanceof Class) {
					RestRouter.getWriters().register(type, (Class<? extends HttpResponseWriter>) writer);
				}
				else {
					RestRouter.getWriters().register(type, (HttpResponseWriter<?>) writer);
				}
			});
		}

		// register exception handlers
		if (exceptionHandlers.size() > 0) {
			exceptionHandlers.forEach(handler -> {

				if (handler instanceof Class) {
					RestRouter.getExceptionHandlers().register((Class<? extends ExceptionHandler>) handler);
				} else {
					RestRouter.getExceptionHandlers().register((ExceptionHandler) handler);
				}
			});
		}

		if (notFound != null && notFound.size() > 0) {

			for (String path : notFound.keySet()) {

				Object notFoundHandler = notFound.get(path);
				if (notFoundHandler instanceof Class) {
					RestRouter.notFound(output, path, (Class<? extends NotFoundResponseWriter>) notFoundHandler);
				}
				else {
					RestRouter.notFound(output, path, (NotFoundResponseWriter) notFoundHandler);
				}
			}
		}

		if (corsHandler != null) {
			output.route().handler(corsHandler);
		}

		return output;
	}
}

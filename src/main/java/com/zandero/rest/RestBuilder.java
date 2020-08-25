package com.zandero.rest;

import com.zandero.rest.bean.BeanProvider;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.rest.writer.NotFoundResponseWriter;
import com.zandero.utils.ArrayUtils;
import com.zandero.utils.Assert;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import javax.validation.Validator;
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
	private Map<Class<?>, Object> registeredProviders = new HashMap<>();

	private List<Object> exceptionHandlers = new ArrayList<>();

	private Map<MediaType, Object> mediaTypeResponseWriters = new LinkedHashMap<>();
	private Map<Class<?>, Object> classResponseWriters = new LinkedHashMap<>();

	private Map<MediaType, Object> mediaTypeValueReaders = new LinkedHashMap<>();
	private Map<Class<?>, Object> classValueReaders = new LinkedHashMap<>();

	/**
	 * Map of path / not found handlers
	 */
	private Map<String, Object> notFound = new LinkedHashMap<>();

	/**
	 * CORS handler if desired
	 */
	private CorsHandler corsHandler = null;

	/**
	 * Body handler if desired
	 */
	private BodyHandler bodyHandler = null;

	/**
	 * Auth provider
	 */
	private AuthorizationProvider authProvider = null;

	/**
	 * Injected class provider
	 */
	private InjectionProvider injectionProvider = null;

	/**
	 * Bean provisioning
	 */
	private BeanProvider beanProvider = null;

	/**
	 * Validation
	 */
	private Validator validator = null;

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

	// Hidden from public view for now
	private RestBuilder register(String... namespace) {

		Assert.notNullOrEmpty(namespace, "Missing REST API namespaces!");

		// TODO finds REST APIs on given class namespaces ...

		return this;
	}

	public RestBuilder notFound(String regExPath, Class<? extends NotFoundResponseWriter> writer) {

		Assert.notNullOrEmptyTrimmed(regExPath, "Missing regEx path!");
		Assert.notNull(writer, "Missing not fount response writer!");

		notFound.put(regExPath, writer); // adds route regExPath prefix
		return this;
	}

	public RestBuilder notFound(Class<? extends NotFoundResponseWriter> writer) {

		Assert.notNull(writer, "Missing not fount response writer!");

		notFound.put(null, writer); // default ... handles all
		return this;
	}

	/**
	 * Enables CORS for all methods and headers /
	 * intended for testing purposes only - not recommended for production use
	 * @return self
	 */
	public RestBuilder enableCors() {

		Set<String> allowedHeaders = new HashSet<>();
		allowedHeaders.add("Access-Control-Allow-Origin");
		//allowedHeaders.add("Access-Control-Allow-Credentials");
		allowedHeaders.add("Access-Control-Allow-Headers");
		allowedHeaders.add("Access-Control-Allow-Methods");
		allowedHeaders.add("Access-Control-Expose-Headers");
		allowedHeaders.add("Access-Control-Request-Method");
		allowedHeaders.add("Access-Control-Request-Headers");
		//allowedHeaders.add("Access-Control-Max-Age");
		allowedHeaders.add("Origin");
		return enableCors("*", false, -1, allowedHeaders, null);
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
	                              List<HttpMethod> methods) {

		corsHandler = CorsHandler.create(allowedOriginPattern)
		                         .allowCredentials(allowCredentials)
		                         .maxAgeSeconds(maxAge);

		if (methods == null || methods.size() == 0) { // if not given than all
			methods = HttpMethod.values();
		}

		for (HttpMethod method : methods) {
			corsHandler.allowedMethod(method);
		}

		if (allowedHeaders.size() > 0) {
			corsHandler.allowedHeaders(allowedHeaders);
		}

		return this;
	}

	public RestBuilder bodyHandler(BodyHandler handler) {
		bodyHandler = handler;
		return this;
	}

	/**
	 * Registeres one or more exception handler classes
	 *
	 * @param handlers to be registered
	 * @return builder
	 */
	@SafeVarargs
	public final RestBuilder errorHandler(Class<? extends ExceptionHandler<?>>... handlers) {

		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");

		exceptionHandlers.addAll(Arrays.asList(handlers));
		return this;
	}

	/**
	 * Registeres one or more exception handler instances
	 *
	 * @param handlers to be registered
	 * @return builder
	 */
	public RestBuilder errorHandler(ExceptionHandler<?>... handlers) {
		Assert.notNullOrEmpty(handlers, "Missing exception handler(s)!");

		exceptionHandlers.addAll(Arrays.asList(handlers));
		return this;
	}

	public RestBuilder writer(Class<? extends HttpResponseWriter<?>> writer) {

		Assert.notNull(writer, "Missing response writer type class!");

		classResponseWriters.put(null, writer);
		return this;
	}

	public RestBuilder writer(HttpResponseWriter<?> writer) {

		Assert.notNull(writer, "Missing response writer type class!");

		classResponseWriters.put(null, writer);
		return this;
	}

	public RestBuilder writer(Class<?> clazz, Class<? extends HttpResponseWriter<?>> writer) {

		Assert.notNull(clazz, "Missing response class!");
		Assert.notNull(writer, "Missing response writer type class!");

		classResponseWriters.put(clazz, writer);
		return this;
	}

	public RestBuilder writer(String mediaType, Class<? extends HttpResponseWriter<?>> writer) {

		Assert.notNullOrEmptyTrimmed(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer class!");

		MediaType type = MediaTypeHelper.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		mediaTypeResponseWriters.put(type, writer);
		return this;
	}

	public RestBuilder writer(MediaType mediaType, Class<? extends HttpResponseWriter<?>> writer) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer class!");

		mediaTypeResponseWriters.put(mediaType, writer);
		return this;
	}

	public RestBuilder reader(Class<?> clazz, Class<? extends ValueReader<?>> reader) {

		Assert.notNull(clazz, "Missing read in class!");
		Assert.notNull(reader, "Missing request reader type class!");

		classValueReaders.put(clazz, reader);
		return this;
	}

	public RestBuilder reader(String mediaType, Class<? extends ValueReader<?>> reader) {

		Assert.notNullOrEmptyTrimmed(mediaType, "Missing media type!");
		Assert.notNull(reader, "Missing value reader class!");

		MediaType type = MediaTypeHelper.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		mediaTypeValueReaders.put(type, reader);
		return this;
	}

	public RestBuilder reader(MediaType mediaType, Class<? extends ValueReader<?>> reader) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(reader, "Missing value reader class!");

		mediaTypeValueReaders.put(mediaType, reader);
		return this;
	}

	public RestBuilder reader(Class<? extends ValueReader<?>> reader) {

		Assert.notNull(reader, "Missing value reader class!");

		mediaTypeValueReaders.put(null, reader);
		return this;
	}

	public RestBuilder reader(ValueReader<?> reader) {

		Assert.notNull(reader, "Missing value reader class!");

		mediaTypeValueReaders.put(null, reader);
		return this;
	}

	/**
	 * Creates a provider handler into routing
	 * @param provider to be executed on every request
	 * @param <T> provided object to insert into @Context
	 * @return builder
	 */
	public <T> RestBuilder provide(ContextProvider<T> provider) {

		Assert.notNull(provider, "Missing context provider!");

		contextProviders.add(provider);
		return this;
	}

	/**
	 * Creates a provider handler by type into routing
	 * @param provider to be executed on every request
	 * @param <T> provided object to insert into @Context
	 * @return builder
	 */
	public <T> RestBuilder provide(Class<? extends ContextProvider<T>> provider) {

		Assert.notNull(provider, "Missing context provider!");

		contextProviders.add(provider);
		return this;
	}

	/**
	 * Creates a provider that delivers type when needed
	 * @param clazz to be provided
	 * @param provider to be executed when needed
	 * @param <T> provided object as argument
	 * @return builder
	 */
	public <T> RestBuilder addProvider(Class<T> clazz, ContextProvider<T> provider) {

		Assert.notNull(clazz, "Missing provided class type!");
		Assert.notNull(provider, "Missing context provider!");
		registeredProviders.put(clazz, provider);
		return this;
	}

	/**
	 * Creates a provider that delivers type when needed
	 * @param provider to be executed when needed
	 * @param <T> provided object as argument
	 * @return builder
	 */
	public <T> RestBuilder addProvider(ContextProvider<T> provider) {

		Assert.notNull(provider, "Missing context provider!");
		registeredProviders.put(null, provider);
		return this;
	}

	/**
	 * Creates a provider that delivers type when needed
	 * @param clazz to be provided
	 * @param provider to be executed when needed
	 * @param <T> provided object as argument
	 * @return builder
	 */
	public <T> RestBuilder addProvider(Class<T> clazz, Class<? extends ContextProvider<T>> provider) {

		Assert.notNull(clazz, "Missing provided class type!");
		Assert.notNull(provider, "Missing context provider!");
		registeredProviders.put(clazz, provider);
		return this;
	}

	/**
	 * Creates a provider that delivers type when needed
	 * @param provider to be executed when needed
	 * @param <T> provided object as argument
	 * @return builder
	 */
	public <T> RestBuilder addProvider(Class<? extends ContextProvider<T>> provider) {

		Assert.notNull(provider, "Missing context provider!");
		registeredProviders.put(null, provider);
		return this;
	}

	/**
	 * Associate provider to getInstance members into REST classes, Reader, Writers ...
	 *
	 * @param provider to do the injection
	 * @return rest builder
	 */
	public RestBuilder injectWith(InjectionProvider provider) {
		injectionProvider = provider;
		return this;
	}

	/**
	 * Associate provider to getInstance members into REST classes, Reader, Writers ...
	 *
	 * @param provider to do the injection
	 * @return rest builder
	 */
	public RestBuilder provideWith(BeanProvider provider) {
		beanProvider = provider;
		return this;
	}

	public RestBuilder provideWith(Class<? extends BeanProvider> provider) {
		try {
			beanProvider = (BeanProvider) ClassFactory.newInstanceOf(provider);
		}
		catch (ClassFactoryException e) {
			throw new IllegalArgumentException(e);
		}

		return this;
	}

	public RestBuilder validateWith(Validator provider) {
		validator = provider;
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

	public RestBuilder setAuthProvider(AuthorizationProvider provider) {
		authProvider = provider;
		return this;
	}

	private Router getRouter(Object... handlers) {

		if (vertx == null) {
			return RestRouter.register(router, handlers);
		}

		return RestRouter.register(vertx, handlers);
	}

	@SuppressWarnings("unchecked")
	public Router build() {

		Assert.notNullOrEmpty(apis, "No REST API given, register at least one! Use: .register(api) call!");

		if (injectionProvider != null) { // prevent WARN log if no provider is given
			RestRouter.injectWith(injectionProvider);
		}

		if (beanProvider != null) {
			RestRouter.provideWith(beanProvider);
		}

		if (validator != null) { // prevent WARN log if no validator is given
			RestRouter.validateWith(validator);
		}

		if (registeredProviders.size() > 0) {

			registeredProviders.forEach((clazz, provider) -> {

				if (provider instanceof Class) {
					if (clazz == null) {
						RestRouter.addProvider((Class<? extends ContextProvider<?>>) provider);
					}
					else {
						RestRouter.addProvider(clazz, (Class<? extends ContextProvider<?>>) provider);
					}
				} else {
					if (clazz == null) {
						RestRouter.addProvider((ContextProvider<?>) provider);
					}
					else {
						RestRouter.addProvider(clazz, (ContextProvider<?>) provider);
					}
				}
			});
		}

		// put CORS handler in front of other handlers
		Object[] handlers = null;
		if (corsHandler != null) {
			handlers = new Object[]{corsHandler};
		}

		if (bodyHandler != null) {
			RestRouter.setBodyHandler(bodyHandler);
		}

		if (authProvider != null) {
			RestRouter.setAuthProvider(authProvider);
		}

		// register all handlers and APIs
		Object[] joined = ArrayUtils.join(handlers, apis.toArray());
		Router output = getRouter(joined);

		contextProviders.forEach(provider -> {
			if (provider instanceof Class) {
				RestRouter.provide(output, (Class<? extends ContextProvider<?>>) provider);
			} else {
				RestRouter.provide(output, (ContextProvider<?>) provider);
			}
		});

		// register readers
		classValueReaders.forEach((clazz, reader) -> {

			if (reader instanceof Class) {
				if (clazz == null) {
					RestRouter.getReaders().register((Class<? extends ValueReader<?>>) reader);
				}
				else {
					RestRouter.getReaders().register(clazz, (Class<? extends ValueReader<?>>) reader);
				}
			} else {
				if (clazz == null) {
					RestRouter.getReaders().register((ValueReader<?>) reader);
				}
				else {
					RestRouter.getReaders().register(clazz, (ValueReader<?>) reader);
				}
			}
		});

		mediaTypeValueReaders.forEach((type, reader) -> {

			if (reader instanceof Class) {
				if (type == null) {
					RestRouter.getReaders().register((Class<? extends ValueReader<?>>) reader);
				}
				else {
					RestRouter.getReaders().register(type, (Class<? extends ValueReader<?>>) reader);
				}
			} else {
				if (type == null) {
					RestRouter.getReaders().register((ValueReader<?>)reader);
				}
				else {
					RestRouter.getReaders().register(type, (ValueReader<?>) reader);
				}
			}
		});

		// register writers
		classResponseWriters.forEach((clazz, writer) -> {

			if (writer instanceof Class) {
				if (clazz == null) {
					RestRouter.getWriters().register((Class<? extends HttpResponseWriter<?>>) writer);
				}
				else {
					RestRouter.getWriters().register(clazz, (Class<? extends HttpResponseWriter<?>>) writer);
				}
			} else {
				if (clazz == null) {
					RestRouter.getWriters().register((HttpResponseWriter<?>) writer);
				} else {
					RestRouter.getWriters().register(clazz, (HttpResponseWriter<?>) writer);
				}
			}
		});

		mediaTypeResponseWriters.forEach((type, writer) -> {
			if (writer instanceof Class) {
				if (type == null) {
					RestRouter.getWriters().register((Class<? extends HttpResponseWriter<?>>) writer);
				}
				else {
					RestRouter.getWriters().register(type, (Class<? extends HttpResponseWriter<?>>) writer);
				}
			} else {
				if (type == null) {
					RestRouter.getWriters().register((HttpResponseWriter<?>) writer);
				}
				else {
					RestRouter.getWriters().register(type, (HttpResponseWriter<?>) writer);
				}
			}
		});

		// register exception handlers
		exceptionHandlers.forEach(handler -> {
			if (handler instanceof Class) {
				RestRouter.getExceptionHandlers().register((Class<? extends ExceptionHandler<?>>) handler);
			} else {
				RestRouter.getExceptionHandlers().register((ExceptionHandler<?>) handler);
			}
		});

		for (String path : notFound.keySet()) {
			Object notFoundHandler = notFound.get(path);
			if (notFoundHandler instanceof Class) {
				RestRouter.notFound(output, path, (Class<? extends NotFoundResponseWriter>) notFoundHandler);
			} else {
				RestRouter.notFound(output, path, (NotFoundResponseWriter) notFoundHandler);
			}
		}

		return output;
	}
}

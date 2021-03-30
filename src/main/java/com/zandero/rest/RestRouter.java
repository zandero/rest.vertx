package com.zandero.rest;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.authorization.RoleBasedUserAuthorizationProvider;
import com.zandero.rest.bean.*;
import com.zandero.rest.cache.*;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.data.*;
import com.zandero.rest.events.RestEventExecutor;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.provisioning.*;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.*;
import com.zandero.utils.Assert;
import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.*;
import org.slf4j.*;

import javax.validation.*;
import javax.validation.executable.ExecutableValidator;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.*;
import java.util.*;

import static com.zandero.rest.provisioning.ClassUtils.*;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

    public static final int ORDER_CORS_HANDLER = -10;
    public static final int ORDER_PROVIDER_HANDLER = -5;

    private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

    private static final ClassForge forge = new ClassForge();

    private static final RestEventExecutor eventExecutor = new RestEventExecutor();

    private static BeanProvider beanProvider = new DefaultBeanProvider();

    private static Validator validator;

    private static BodyHandler bodyHandler;

    /**
     * Default authentication / credential and authorization providers
     * Used in case no other provider is defined
     */
    private static RestAuthenticationProvider defaultAuthenticationProvider;
    private static AuthorizationProvider defaultAuthorizationProvider;

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

            // check if api is an instance of a class or a class type
            if (api instanceof Class) {

                Class<?> inspectApi = (Class<?>) api;

                try {
                    api = ClassFactory.newInstanceOf(inspectApi, getInjectionProvider(), null);
                } catch (ClassFactoryException | ContextException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }

            // if handler than only register and move on
            if (api instanceof Handler) {
                handler(router, (Handler<RoutingContext>) api);
                continue;
            }

            // REST endpoints
            Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(api.getClass());

            for (RouteDefinition definition : definitions.keySet()) {

                // bind method execution
                Route route;
                if (definition.pathIsRegEx()) {
                    route = router.routeWithRegex(definition.getMethod(), definition.getRoutePath());
                } else {
                    route = router.route(definition.getMethod(), definition.getRoutePath());
                }
                //
                log.info("Registering route: " + definition);

                if (definition.getOrder() != 0) {
                    route.order(definition.getOrder());
                }

                // each route gets ist definition provided via context provider
                ContextProvider<RouteDefinition> definitionHandler = request -> definition;
                route.handler(getContextHandler(definitionHandler));

                //
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

                // check body and reader compatibility beforehand
                checkBodyReader(definition);

                // add BodyHandler in case request has a body ...
                if (definition.requestHasBody()) {
                    if (bodyHandler == null) {
                        route.handler(BodyHandler.create());
                        log.debug("Adding default body handler to route!");
                    } else {
                        route.handler(bodyHandler);
                        log.debug("Adding provided body handler to route!");
                    }
                }

                // Authentication
                if (definition.getAuthenticationProvider() != null || defaultAuthenticationProvider != null) {
                    route.handler(getAuthenticationProvider(definition.getAuthenticationProvider(),
                                                            definition));
                }

                // Authorization
                if (definition.getAuthorizationProvider() != null || defaultAuthorizationProvider != null) {
                    route.handler(getAuthorizationHandler(definition.getAuthorizationProvider(),
                                                          definition));
                } else if (definition.checkSecurity()) {
                    // for back compatibility purposes
                    // add security check handler in front of regular route handler
                    // (in case @PermitAll, @DenyAll or @RolesAllowed is used)
                    route.handler(getAuthorizationHandler(RoleBasedUserAuthorizationProvider.class,
                                                          definition));
                }

                // bind handler // blocking or async
                Handler<RoutingContext> handler;
                Method method = definitions.get(definition);

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

    private static void checkWriterCompatibility(RouteDefinition definition) {
        try { // no way to know the accept content at this point
            forge.getResponseWriter(definition.getReturnType(), definition, null);
        } catch (ClassFactoryException e) {
            // ignoring instance creation ... but leaving Illegal argument exceptions to pass
        }
    }

    public static void provide(Router output, Class<? extends ContextProvider<?>> provider) {

        try {
            Class<?> clazz = (Class<?>) getGenericType(provider);
            ContextProvider<?> instance = forge.getContextProvider(clazz,
                                                                   provider,
                                                                   null);
            // set before other routes ...
            output.route().order(ORDER_PROVIDER_HANDLER).handler(getContextHandler(instance));
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void provide(Router output, ContextProvider<?> provider) {

        output.route().order(ORDER_PROVIDER_HANDLER).handler(getContextHandler(provider));
    }

    private static Handler<RoutingContext> getContextHandler(ContextProvider<?> instance) {

        return context -> {

            if (instance != null) {
                try {
                    Object provided = instance.provide(context.request());

                    if (provided instanceof User) {
                        context.setUser((User) provided);
                    }

                    if (provided instanceof Session) {
                        context.setSession((Session) provided);
                    }

                    if (provided != null) { // push provided context into request data
                        context.data().put(ContextProvider.getDataKey(provided), provided);
                    }
                } catch (Throwable e) {
                    handleException(e, context, null); // no definition
                }
            }

            context.next();
        };
    }

    @SuppressWarnings("unchecked")
    public static void handler(Router output, Class<? extends Handler<RoutingContext>> handler) {

        try {
            Handler<RoutingContext> instance = (Handler<RoutingContext>) ClassFactory.newInstanceOf(handler, getInjectionProvider(), null);
            output.route().handler(instance);
        } catch (ClassFactoryException | ContextException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static void handler(Router output, Handler<RoutingContext> handler) {
        output.route().handler(handler);
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
     * Handles not found route in case request regExPath mathes given regExPath prefix
     *
     * @param router    to add route to
     * @param regExPath prefix
     * @param notFound  handler
     */
    public static void notFound(Router router, String regExPath, NotFoundResponseWriter notFound) {

        Assert.notNull(router, "Missing router!");
        Assert.notNull(notFound, "Missing not found handler!");

        addLastHandler(router, regExPath, getNotFoundHandler(notFound));
    }

    /**
     * Handles not found route in case request regExPath mathes given regExPath prefix
     *
     * @param router    to add route to
     * @param regExPath prefix
     * @param notFound  hander
     */
    public static void notFound(Router router, String regExPath, Class<? extends NotFoundResponseWriter> notFound) {

        Assert.notNull(router, "Missing router!");
        Assert.notNull(notFound, "Missing not found handler!");

        addLastHandler(router, regExPath, getNotFoundHandler(notFound));
    }

    private static void addLastHandler(Router router, String path, Handler<RoutingContext> notFoundHandler) {
        if (path == null) {
            router.route().last().handler(notFoundHandler);
        } else {
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
    public static void enableCors(Router router,
                                  String allowedOriginPattern,
                                  boolean allowCredentials,
                                  int maxAge,
                                  Set<String> allowedHeaders,
                                  HttpMethod... methods) {

        CorsHandler handler = CorsHandler.create(allowedOriginPattern)
                                  .allowCredentials(allowCredentials)
                                  .maxAgeSeconds(maxAge);

        if (methods == null || methods.length == 0) { // if not given than all
            methods = HttpMethod.values().toArray(new HttpMethod[]{});
        }

        for (HttpMethod method : methods) {
            handler.allowedMethod(method);
        }

        handler.allowedHeaders(allowedHeaders);
        router.route().order(ORDER_CORS_HANDLER).handler(handler);
    }

    public static void authenticateWith(Class<? extends RestAuthenticationProvider> provider) {
        try {
            Assert.notNull(provider, "Missing authorization provider!");
            Assert.isNull(defaultAuthenticationProvider, "Default authentication provider already defined!");
            defaultAuthenticationProvider = forge.getAuthenticationProvider(provider, null);

        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void authenticateWith(RestAuthenticationProvider provider) {
        Assert.notNull(provider, "Missing authorization provider!");
        Assert.isNull(defaultAuthenticationProvider, "Default authentication provider already defined!");
        defaultAuthenticationProvider = provider;
    }

    public static void authorizeWith(Class<? extends AuthorizationProvider> provider) {
        try {
            Assert.notNull(provider, "Missing authorization provider!");
            Assert.isNull(defaultAuthorizationProvider, "Default authorization provider already defined!");
            defaultAuthorizationProvider = forge.getAuthorizationProvider(provider, null);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void authorizeWith(AuthorizationProvider provider) {
        Assert.notNull(provider, "Missing authorization provider!");
        Assert.isNull(defaultAuthorizationProvider, "Default authorization provider already defined!");
        defaultAuthorizationProvider = provider;
    }

    private static void checkBodyReader(RouteDefinition definition) {

        if (!definition.requestCanHaveBody() || !definition.hasBodyParameter()) {
            return;
        }

        ValueReader<?> bodyReader = forge.getValueReader(definition.getBodyParameter(),
                                                         definition.getReader(),
                                                         null,
                                                         definition.getConsumes());

        if (bodyReader != null && definition.checkCompatibility()) {

            Type readerType = getGenericType(bodyReader.getClass());
            MethodParameter bodyParameter = definition.getBodyParameter();

            checkIfCompatibleType(bodyParameter.getDataType(), readerType,
                                  definition.toString().trim() + " - Parameter type: '" +
                                      bodyParameter.getDataType() + "' not matching reader type: '" +
                                      readerType + "' in: '" + bodyReader.getClass() + "'!");
        }
    }

    private static Handler<RoutingContext> getAuthenticationProvider(Class<? extends RestAuthenticationProvider> authenticatorProviderClass,
                                                                     RouteDefinition definition) {
        return context -> {
            try {
                RestAuthenticationProvider authenticator = authenticatorProviderClass != null ?
                                                               forge.getAuthenticationProvider(authenticatorProviderClass, context) :
                                                               defaultAuthenticationProvider;

                authenticator.authenticate(context, userAsyncResult -> {
                    if (userAsyncResult.failed()) {
                        Throwable ex = (userAsyncResult.cause() != null ?
                                            userAsyncResult.cause() :
                                            new UnauthorizedException(context.user()));
                        handleException(ex, context, definition);
                    } else {
                        context.setUser(userAsyncResult.result());
                        context.next();
                    }
                });
            } catch (Throwable e) {
                log.error("Authentication failed: " + e.getMessage(), e);
                handleException(e, context, definition);
            }
        };
    }

    private static Handler<RoutingContext> getAuthorizationHandler(Class<? extends AuthorizationProvider> providerClass, RouteDefinition definition) {
        return context -> {

            try {
                AuthorizationProvider provider = providerClass != null ?
                                                     forge.getAuthorizationProvider(providerClass, context) :
                                                     defaultAuthorizationProvider;

                provider.getAuthorizations(context.user(), userAuthorizationResult -> {
                    if (userAuthorizationResult.failed()) {
                        Throwable ex = (userAuthorizationResult.cause() != null ?
                                            userAuthorizationResult.cause() :
                                            new ForbiddenException(context.user()));
                        handleException(ex, context, definition);
                    } else {
                        context.next();
                    }
                });
            } catch (Throwable e) {
                log.error("Authorization failed: " + e.getMessage(), e);
                handleException(e, context, definition);
            }
        };
    }

    private static Handler<RoutingContext> getHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

        return context -> context.vertx().executeBlocking(
            fut -> {
                try {
                    Object[] args = ArgumentProvider.getArguments(method,
                                                                  definition,
                                                                  context,
                                                                  beanProvider,
                                                                  forge);

                    validate(method, definition, validator, toInvoke, args);

                    fut.complete(method.invoke(toInvoke, args));
                } catch (Throwable e) {
                    fut.fail(e);
                }
            },
            definition.executeBlockingOrdered(), // false by default
            res -> {
                if (res.succeeded()) {
                    try {
                        Object result = res.result();

                        Class returnType = result != null ? result.getClass() : definition.getReturnType();

                        HttpResponseWriter writer = forge.getResponseWriter(returnType,
                                                                            definition,
                                                                            context);

                        validateResult(result, method, definition, validator, toInvoke);
                        produceResponse(result, context, definition, writer);
                    } catch (Throwable e) {
                        handleException(e, context, definition);
                    }
                } else {
                    handleException(res.cause(), context, definition);
                }
            }
        );
    }

    private static Handler<RoutingContext> getAsyncHandler(final Object toInvoke, final RouteDefinition definition, final Method method) {

        return context -> {

            try {
                Object[] args = ArgumentProvider.getArguments(method,
                                                              definition,
                                                              context,
                                                              beanProvider,
                                                              forge);

                validate(method, definition, validator, toInvoke, args);

                Object result = method.invoke(toInvoke, args);

                // get future from promise ...
                if (result instanceof Promise) {
                    Promise<?> pro = (Promise) result;
                    result = pro.future();
                }

                if (result instanceof Future) {
                    Future<?> fut = (Future) result;

                    // wait for future to complete ... don't block vertx event bus in the mean time
                    fut.onComplete(handler -> {

                        if (fut.succeeded()) {

                            try {
                                Object futureResult = fut.result();

                                HttpResponseWriter writer;
                                if (futureResult != null) { // get writer from result type otherwise we don't know
                                    writer = forge.getResponseWriter(futureResult.getClass(),
                                                                     definition,
                                                                     context);
                                } else { // due to limitations of Java generics we can't tell the type if response is null
                                    writer = (definition.getWriter() != null) ?
                                                 (HttpResponseWriter) ClassFactory.newInstanceOf(definition.getWriter()) : new GenericResponseWriter();
                                }

                                validateResult(futureResult, method, definition, validator, toInvoke);
                                produceResponse(futureResult, context, definition, writer);
                            } catch (Throwable e) {
                                handleException(e, context, definition);
                            }
                        } else {
                            handleException(fut.cause(), context, definition);
                        }
                    });
                }
            } catch (Throwable e) {
                handleException(e, context, definition);
            }
        };
    }

    private static void validate(Method method, RouteDefinition definition, Validator validator, Object toInvoke, Object[] args) {

        // check method params first (if any)
        if (validator != null && args != null) {
            ExecutableValidator executableValidator = validator.forExecutables();
            Set<ConstraintViolation<Object>> result = executableValidator.validateParameters(toInvoke, method, args);
            if (result != null && result.size() > 0) {
                throw new ConstraintException(definition, result);
            }
        }
    }

    private static void validateResult(Object result, Method method, RouteDefinition definition, Validator validator, Object toInvoke) {

        if (validator != null) {
            ExecutableValidator executableValidator = validator.forExecutables();
            Set<ConstraintViolation<Object>> validationResult = executableValidator.validateReturnValue(toInvoke, method, result);
            if (validationResult != null && validationResult.size() > 0) {
                throw new ConstraintException(definition, validationResult);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Handler<RoutingContext> getNotFoundHandler(Object notFoundWriter) {
        return context -> {

            try {
                // fill up definition (response headers) from request
                RouteDefinition definition = new RouteDefinition(context);

                HttpResponseWriter<?> writer;
                if (notFoundWriter instanceof Class) {
                    writer = (HttpResponseWriter<?>) ClassProducer.getClassInstance((Class<?>) notFoundWriter, getWriters(), getInjectionProvider(), context);
                } else {
                    writer = (HttpResponseWriter<?>) notFoundWriter;
                }

                produceResponse(null, context, definition, writer);
            } catch (Throwable e) {
                handleException(e, context, null);
            }
        };
    }

    private static void handleException(Throwable e, RoutingContext context, final RouteDefinition definition) {

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

            handler = forge.getExceptionHandler(clazz, exHandlers, context);
        } catch (ClassFactoryException classException) {
            // Can't provide exception handler ... rethrow
            log.error("Can't provide exception handler!", classException);
            // fall back to generic ...
            handler = new GenericExceptionHandler();
            ex = new ExecuteException(500, classException);
        } catch (ContextException contextException) {
            // Can't provide @Context for handler ... rethrow
            log.error("Can't provide @Context!", contextException);
            // fall back to generic ...
            handler = new GenericExceptionHandler();
            ex = new ExecuteException(500, contextException);
        }

        if (handler instanceof GenericExceptionHandler) {
            log.error("Handling exception: ", e);
        } else {
            log.debug("Handling exception, with: " + handler.getClass().getName(), e);
        }

        HttpServerResponse response = context.response();
        response.setStatusCode(ex.getStatusCode());

        HttpServerRequest request = context.request();
        MediaType accept = MediaTypeHelper.valueOf(request.getHeader(HttpHeaders.ACCEPT));

        handler.addResponseHeaders(definition, accept, response);

        try {
            handler.write(ex.getCause(), request, response);

            eventExecutor.triggerEvents(ex.getCause(), response.getStatusCode(), definition, context,
                                        getInjectionProvider());
        } catch (Throwable handlerException) {
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
    private static void produceResponse(Object result,
                                        RoutingContext context,
                                        RouteDefinition definition,
                                        HttpResponseWriter writer) throws Throwable {

        HttpServerResponse response = context.response();
        HttpServerRequest request = context.request();
        MediaType accept = MediaTypeHelper.valueOf(request.getHeader(HttpHeaders.ACCEPT));

        // add default response headers per definition (or from writer definition)
        writer.addResponseHeaders(definition, accept, response);
        writer.write(result, request, response);

        // find and trigger events from // result / response
        eventExecutor.triggerEvents(result, response.getStatusCode(), definition, context, getInjectionProvider());

        // finish if not finished by writer
        // and is not an Async REST (Async RESTs must finish responses on their own)
        if (!definition.isAsync() && !response.ended()) {
            response.end();
        }
    }

    public static WriterCache getWriters() {
        return forge.getWriters();
    }

    public static ReaderCache getReaders() {
        return forge.getReaders();
    }

    public static ExceptionHandlerCache getExceptionHandlers() {
        return forge.getExceptionHandlers();
    }

    public static ContextProviderCache getContextProviders() {
        return forge.getContextProviders();
    }

    public static AuthenticationProvidersCache getAuthenticationProviders() {
        return forge.getAuthenticationProviders();
    }

    public static AuthorizationProvidersCache getAuthorizationProviders() {
        return forge.getAuthorizationProviders();
    }

    public static InjectionProvider getInjectionProvider() {
        return forge.getInjectionProvider();
    }

    /**
     * Clears all cached classes and removes any associated validator and injection provider
     * Intended for Unit tests only, should not be called in production code
     */
    public static void clearCache() {
        forge.clean();

        defaultAuthorizationProvider = null;
        defaultAuthenticationProvider = null;

        validateWith((Validator) null);
        injectWith((InjectionProvider) null);
    }

    /**
     * Registers a context provider for given type of class
     *
     * @param provider clazz type to be registered
     */
    public static void addProvider(Class<? extends ContextProvider<?>> provider) {

        Class<?> clazz = (Class<?>) getGenericType(provider);
        addProvider(clazz, provider);
    }

    public static void addProvider(Class<?> clazz, Class<? extends ContextProvider<?>> provider) {

        Assert.notNull(clazz, "Missing provided class type!");
        Assert.notNull(provider, "Missing provider class type!!");

        getContextProviders().register(clazz, provider);
        log.info("Registering '" + clazz + "' provider '" + provider.getName() + "'");
    }

    public static void addProvider(ContextProvider<?> provider) {

        Class<?> clazz = (Class<?>) getGenericType(provider.getClass());
        addProvider(clazz, provider);
    }

    public static void addProvider(Class<?> clazz, ContextProvider<?> provider) {

        Assert.notNull(clazz, "Missing provider class type!");
        Assert.notNull(provider, "Missing provider instance!");

        getContextProviders().register(clazz, provider);
        log.info("Registering '" + clazz + "' provider '" + provider.getClass().getName() + "'");
    }

    static void pushContext(RoutingContext context, Object object) {

        Assert.notNull(context, "Missing context!");
        Assert.notNull(object, "Can't push null into context!");

        context.put(ContextProvider.getDataKey(object), object);
    }

    /**
     * Provide an injector to getInstance classes where needed
     *
     * @param provider to create/inject classes
     */
    public static void injectWith(InjectionProvider provider) {

        forge.setInjectionProvider(provider);
        if (getInjectionProvider() != null) {
            log.info("Registered injection provider: " + getInjectionProvider().getClass().getName());
        } else {
            log.info("No injection provider specified!");
        }
    }

    /**
     * Provide an injector to getInstance classes where needed
     *
     * @param provider class type
     */
    public static void injectWith(Class<InjectionProvider> provider) {

        try {
            forge.setInjectionProvider((InjectionProvider) ClassFactory.newInstanceOf(provider));
            log.info("Registered injection provider: " + getInjectionProvider().getClass().getName());
        } catch (ClassFactoryException e) {
            log.error("Failed to instantiate injection provider: ", e);
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * Provide an injector to getInstance classes where needed
     *
     * @param provider to create/inject classes
     */
    public static void provideWith(BeanProvider provider) {

        beanProvider = provider;
        if (beanProvider != null) {
            log.info("Registered bean provider: " + beanProvider.getClass().getName());
        } else {
            log.info("No bean provider specified!");
        }
    }

    /**
     * Provide an injector to getInstance classes where needed
     *
     * @param provider class type
     */
    public static void provideWith(Class<BeanProvider> provider) {

        try {
            beanProvider = (BeanProvider) ClassFactory.newInstanceOf(provider);
            log.info("Registered bean provider: " + beanProvider.getClass().getName());
        } catch (ClassFactoryException e) {
            log.error("Failed to instantiate bean provider: ", e);
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
        if (validator != null) {
            log.info("Registered validation provider: " + validator.getClass().getName());
        } else {
            log.info("No validation provider specified!");
        }
    }

    /**
     * Provide an validator to validate arguments
     *
     * @param provider class type
     */
    public static void validateWith(Class<Validator> provider) {

        try {
            validator = (Validator) ClassFactory.newInstanceOf(provider);
            log.info("Registered validation provider: " + validator.getClass().getName());
        } catch (ClassFactoryException e) {
            log.error("Failed to instantiate validation provider: ", e);
            throw new IllegalArgumentException(e);
        }
    }

    public static void setBodyHandler(BodyHandler handler) {

        if (bodyHandler != null) {
            log.error("Body handler already defined, set body handler before any routes!");
            return;
        }

        Assert.notNull(handler, "Missing body handler!");
        bodyHandler = handler;
    }
}

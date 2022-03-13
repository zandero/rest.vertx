package com.zandero.rest.provisioning;

import com.zandero.rest.authentication.RestAuthenticationProvider;
import com.zandero.rest.cache.*;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.reader.*;
import com.zandero.rest.writer.*;
import com.zandero.utils.Assert;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;

import static com.zandero.rest.provisioning.ClassUtils.*;

/**
 * Holds caching and class producing in one place
 */
public class ClassForge {

    private final static Logger log = LoggerFactory.getLogger(ClassForge.class);

    private final WriterCache writers = new WriterCache();
    private final ReaderCache readers = new ReaderCache();
    private final ContextProviderCache contextProviders = new ContextProviderCache();
    private final ExceptionHandlerCache exceptionHandlers = new ExceptionHandlerCache(contextProviders);

    private final AuthorizationProvidersCache authorizationProviders = new AuthorizationProvidersCache();
    private final AuthenticationProvidersCache authenticationProviders = new AuthenticationProvidersCache();

    private InjectionProvider injection;
    private final ContextInjector contextInjector = new ContextInjector(contextProviders);

    public WriterCache getWriters() {
        return writers;
    }

    public ReaderCache getReaders() {
        return readers;
    }

    public ExceptionHandlerCache getExceptionHandlers() {
        return exceptionHandlers;
    }

    public ContextProviderCache getContextProviders() {
        return contextProviders;
    }

    public InjectionProvider getInjectionProvider() {
        return injection;
    }

    public ContextInjector getContextInjector() {
        return contextInjector;
    }

    public AuthorizationProvidersCache getAuthorizationProviders() {
        return authorizationProviders;
    }

    public AuthenticationProvidersCache getAuthenticationProviders() {
        return authenticationProviders;
    }

    public void clean() {
        writers.clear();
        readers.clear();
        exceptionHandlers.clear();
        contextProviders.clear();
        authenticationProviders.clear();
        authorizationProviders.clear();
    }

    public void setInjectionProvider(InjectionProvider provider) {
        injection = provider;
    }

    public RestAuthenticationProvider getAuthenticationProvider(Class<? extends RestAuthenticationProvider> provider,
                                                                RoutingContext context) throws ClassFactoryException, ContextException {
        return (RestAuthenticationProvider) ClassProducer.getClassInstance(provider,
                                                                           authenticationProviders,
                                                                           contextInjector,
                                                                           injection,
                                                                           context);
    }

    public AuthorizationProvider getAuthorizationProvider(Class<? extends AuthorizationProvider> authorizationProvider,
                                                          RoutingContext context) throws ClassFactoryException, ContextException {
        return (AuthorizationProvider) ClassProducer.getClassInstance(authorizationProvider,
                                                                      authorizationProviders,
                                                                      contextInjector,
                                                                      injection,
                                                                      context);
    }

    public void injectContextData(Class<?> dataType,
                                  Class<? extends ContextProvider> provider,
                                  RoutingContext context) throws Throwable {

        // TODO: fix this ... how come provider gets provider ...
        ContextProvider found = getContextProvider(dataType,
                                                   provider,
                                                   context);

        if (found != null) {
            Object result = found.provide(context.request());
            if (result != null) {
                context.data().put(ContextProvider.getDataKey(dataType), result);
            }
        }
    }

    // TODO: check if clazzType and aClass (both are needed in this call)
    public ContextProvider getContextProvider(Class desiredClass,
                                              Class<? extends ContextProvider> contextProvider,
                                              RoutingContext context) throws ClassFactoryException,
                                                                                 ContextException {

        Class<?> clazz = contextProvider;

        // No class defined ... try by type
        if (clazz == null) {
            clazz = contextProviders.getAssociatedType(desiredClass);
        }

        return (ContextProvider) ClassProducer.getClassInstance(clazz, contextProviders, contextInjector, injection, context);
    }

    public ExceptionHandler getExceptionHandler(Class<? extends Throwable> aClass,
                                                Class<? extends ExceptionHandler>[] definitionExHandlers,
                                                RoutingContext context) throws ClassFactoryException, ContextException {

        // trickle down ... from definition to default handler
        // search definition add as given in REST (class or method annotation)
        if (definitionExHandlers != null && definitionExHandlers.length > 0) {

            for (Class<? extends ExceptionHandler> handler : definitionExHandlers) {

                Type type = getGenericType(handler);
                if (checkIfCompatibleType(aClass, type)) {
                    log.info("Found matching exception handler: " + handler.getName());
                    return (ExceptionHandler) ClassProducer.getClassInstance(handler, exceptionHandlers, contextInjector, injection, context);
                }
            }
        }

        ExceptionHandler<?> cached = exceptionHandlers.getInstanceByAssociatedType(aClass);
        if (cached != null) {
            log.trace("Returning cached exception handler: " + cached.getClass().getName());
            return cached;
        }

        Class<? extends ExceptionHandler> found = exceptionHandlers.getAssociatedType(aClass);
        if (found != null) {
            log.info("Found matching exception handler: " + found.getName());
            return (ExceptionHandler) ClassProducer.getClassInstance(found, exceptionHandlers, contextInjector, injection, context);
        }

        for (Class<? extends ExceptionHandler> handler : exceptionHandlers.defaultHandlers.values()) {
            Type type = getGenericType(handler);
            if (checkIfCompatibleType(aClass, type)) {
                log.info("Found matching exception handler: " + handler.getName());
                return (ExceptionHandler) ClassProducer.getClassInstance(handler, exceptionHandlers, contextInjector, injection, context);
            }
        }

        // create class instance
        log.info("Resolving to generic exception handler: " + GenericExceptionHandler.class.getName());
        return (ExceptionHandler) ClassProducer.getClassInstance(GenericExceptionHandler.class, exceptionHandlers, contextInjector, injection, context);
    }

    @Deprecated
    public Object get(Class<?> type,
                      Class<?> byDefinition,
                      RoutingContext routeContext,
                      MediaType[] mediaTypes,
                      MediaTypesClassCache cache) throws ClassFactoryException, ContextException {

        Class<?> clazz = byDefinition;

        // No class defined ... try by type
        if (clazz == null) {
            Object instance = cache.getInstanceByAssociatedType(type);
            if (instance != null) {
                return instance;
            }

            clazz = cache.getAssociatedType(type);
        }

        // try with media type ...
        if (clazz == null && mediaTypes != null && mediaTypes.length > 0) {

            for (MediaType mediaType : mediaTypes) {
                Object instance = cache.getInstanceByAssociatedMediaType(mediaType);
                if (instance != null) {
                    return instance;
                }

                clazz = cache.getAssociatedTypeFromMediaType(mediaType);
                if (clazz != null) {
                    break;
                }
            }
        }

        return ClassProducer.getClassInstance(clazz, cache, contextInjector, injection, routeContext);
    }

    public ValueReader getValueReader(MethodParameter parameter,
                                      RoutingContext context,
                                      MediaType... mediaTypes) {

        return getValueReader(parameter, parameter.getReader(), context, mediaTypes);
    }

    /**
     * Step over all possibilities to provide desired reader
     *
     * @param parameter          check parameter if reader is set or we have a type reader present
     * @param byMethodDefinition check default definition
     * @param context            routing context
     * @param mediaTypes         check by consumes annotation
     * @return found reader or GenericBodyReader
     */
    public ValueReader getValueReader(MethodParameter parameter,
                                      Class<? extends ValueReader> byMethodDefinition,
                                      RoutingContext context,
                                      MediaType... mediaTypes) {

        // by type
        Class<?> readerType = null;
        try {

            // reader parameter as given
            Assert.notNull(parameter, "Missing parameter!");
            Class<? extends ValueReader> reader = parameter.getReader();
            if (reader != null) {
                return (ValueReader) ClassProducer.getClassInstance(reader, readers, contextInjector, injection, context);
            }

            // by value type, if body also by method/class definition or consumes media type
            readerType = parameter.getDataType();

            ValueReader valueReader = (ValueReader) get(readerType, byMethodDefinition, context, mediaTypes, readers);
            return valueReader != null ? valueReader : new GenericValueReader();
        } catch (ClassFactoryException e) {

            log.error("Failed to provide value reader: " + readerType + ", for: " + parameter + ", falling back to GenericBodyReader() instead!");
            return new GenericValueReader();
        } catch (ContextException e) {

            log.error(
                "Failed inject context into value reader: " + readerType + ", for: " + parameter + ", falling back to GenericBodyReader() instead!");
            return new GenericValueReader();
        }
    }


    /**
     * Finds assigned response writer or tries to assign a writer according to produces annotation and result type
     *
     * @param returnType   type of response
     * @param definition   method definition
     * @param routeContext routing context
     * @param accept       accept media type header
     * @return writer to be used to produce response, or {@link GenericResponseWriter} in case no suitable writer could be found
     */
    protected HttpResponseWriter<?> getResponseWriter(Class<?> returnType,
                                                      RouteDefinition definition,
                                                      RoutingContext routeContext,
                                                      MediaType accept) {

        try {
            HttpResponseWriter<?> writer = null;
            if (accept != null) {
                writer = (HttpResponseWriter<?>) get(returnType, definition.getWriter(), routeContext, new MediaType[]{accept}, writers);
            }

            if (writer == null) {
                writer = (HttpResponseWriter<?>) get(returnType, definition.getWriter(), routeContext, definition.getProduces(), writers);
            }

            return writer != null ? writer : new GenericResponseWriter<>();
        } catch (ClassFactoryException e) {
            log.error(
                "Failed to provide response writer: " + returnType + ", for: " + definition + ", falling back to GenericResponseWriter() instead!");
            return new GenericResponseWriter<>();
        } catch (ContextException e) {
            log.error("Could not inject context to provide response writer: " + returnType + ", for: " + definition +
                          ", falling back to GenericResponseWriter() instead!");
            return new GenericResponseWriter<>();
        }
    }

    public HttpResponseWriter<?> getResponseWriter(Class<?> returnType,
                                                   RouteDefinition definition,
                                                   RoutingContext context) throws ClassFactoryException {


        if (returnType == null) {
            returnType = definition.getReturnType();
        }

        MediaType acceptHeader = null;
        if (context != null) {
            acceptHeader = MediaTypeHelper.valueOf(context.getAcceptableContentType());
        }

        HttpResponseWriter<?> writer = getResponseWriter(returnType, definition, context, acceptHeader);

        if (writer == null) {
            log.error("No writer could be provided. Falling back to " + GenericResponseWriter.class.getSimpleName() + " instead!");
            return (HttpResponseWriter<?>) ClassFactory.newInstanceOf(GenericResponseWriter.class);
        }

        if (definition.checkCompatibility() &&
                checkCompatibility(writer.getClass())) {

            Type writerType = getGenericType(writer.getClass());
            checkIfCompatibleType(returnType,
                                  writerType,
                                  definition.toString().trim() + " - Response type: '" +
                                      returnType + "' not matching writer type: '" +
                                      writerType + "' in: '" + writer.getClass() + "'!");
        }

        return writer;
    }
}
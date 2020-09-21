package com.zandero.rest.data;

import com.zandero.rest.cache.WriterCache;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.writer.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;

import static com.zandero.rest.data.ClassUtils.*;

public class ClassForge {

    private final static Logger log = LoggerFactory.getLogger(ClassForge.class);

    // TODO: to be hidden
    private final WriterCache writers = new WriterCache();

    private InjectionProvider injection;

    public WriterCache getWriters() {
        return writers;
    }

    public void setInjectionProvider(InjectionProvider provider) {
        injection = provider;
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
    protected HttpResponseWriter getResponseWriter(Class returnType,
                                                   RouteDefinition definition,
                                                   // InjectionProvider provider,
                                                   RoutingContext routeContext,
                                                   MediaType accept) {

        try {
            HttpResponseWriter writer = null;
            if (accept != null) {
                writer = (HttpResponseWriter) ClassFactory.get(returnType, writers, definition.getWriter(), injection, routeContext, new MediaType[]{accept});
            }

            if (writer == null) {
                writer = (HttpResponseWriter) ClassFactory.get(returnType, writers, definition.getWriter(), injection, routeContext, definition.getProduces());
            }

            return writer != null ? writer : new GenericResponseWriter();
        } catch (ClassFactoryException e) {
            log.error(
                "Failed to provide response writer: " + returnType + ", for: " + definition + ", falling back to GenericResponseWriter() instead!");
            return new GenericResponseWriter();
        } catch (ContextException e) {
            log.error("Could not inject context to provide response writer: " + returnType + ", for: " + definition +
                          ", falling back to GenericResponseWriter() instead!");
            return new GenericResponseWriter();
        }
    }

    public HttpResponseWriter<?> getWriter(Class<?> returnType,
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
/*
    public void register(Class<? extends HttpResponseWriter<?>> writer) {
        writers.register(writer);
    }

    public void register(HttpResponseWriter writer) {
        writers.register(writer);
    }

    public void register(MediaType type, HttpResponseWriter<?> writer) {
        writers.register(type, writer);
    }*/
}

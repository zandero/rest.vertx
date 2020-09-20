package com.zandero.rest.writer;

import com.zandero.rest.data.*;
import com.zandero.utils.Assert;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 * Provides definition and caching of response writer implementations
 */
public class WriterFactory extends ClassCache<HttpResponseWriter> {

    private final static Logger log = LoggerFactory.getLogger(WriterFactory.class);

    public WriterFactory() {
        // default writers
        setDefaults();
    }

    @Override
    protected void setDefaults() {

        classTypes.put(Response.class, JaxResponseWriter.class);
        classTypes.put(HttpServerResponse.class, VertxResponseWriter.class);

        mediaTypes.put(MediaType.APPLICATION_JSON, JsonResponseWriter.class);
        mediaTypes.put(MediaType.TEXT_HTML, GenericResponseWriter.class);

        // if not found ... default to simple toString() writer
        mediaTypes.put(MediaType.TEXT_PLAIN, PlainResponseWriter.class);
        mediaTypes.put(MediaType.WILDCARD, PlainResponseWriter.class);
    }

    /**
     * Finds assigned response writer or tries to assign a writer according to produces annotation and result type
     *
     * @param returnType   type of response
     * @param definition   method definition
     * @param provider     injection provider if any
     * @param routeContext routing context
     * @param accept       accept media type header
     * @return writer to be used to produce response, or {@link GenericResponseWriter} in case no suitable writer could be found
     *//*
    public HttpResponseWriter getResponseWriter(Class returnType,
                                                RouteDefinition definition,
                                                InjectionProvider provider,
                                                RoutingContext routeContext,
                                                MediaType accept) {

        try {
            HttpResponseWriter writer = null;
            if (accept != null) {
                writer = (HttpResponseWriter) ClassFactory.get(returnType, this, definition.getWriter(), provider, routeContext, new MediaType[]{accept});
            }

            if (writer == null) {
                writer = (HttpResponseWriter) ClassFactory.get(returnType, this, definition.getWriter(), provider, routeContext, definition.getProduces());
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
    }*/

    public void register(Class<? extends HttpResponseWriter> writer) {

        Assert.notNull(writer, "Missing writer type!");
        boolean registered = false;

        Produces found = writer.getAnnotation(Produces.class);
        if (found != null) {
            MediaType[] produces = MediaTypeHelper.getMediaTypes(found.value());
            if (produces != null && produces.length > 0) {
                for (MediaType type : produces) {
                    register(type, writer);
                }
                registered = true;
            }
        }

        Assert.isTrue(registered,
                      "Failed to register writer: '" + writer.getName() + "', missing @Produces annotation!");
    }

    public void register(HttpResponseWriter writer) {

        Assert.notNull(writer, "Missing response writer!");
        boolean registered = false;

        Produces found = writer.getClass().getAnnotation(Produces.class);
        if (found != null) {
            MediaType[] produces = MediaTypeHelper.getMediaTypes(found.value());
            if (produces != null && produces.length > 0) {
                for (MediaType type : produces) {
                    super.register(type, writer); // TODO: might be register without super
                }
                registered = true;
            }
        }

        Assert.isTrue(registered,
                      "Failed to register writer: '" + writer.getClass().getName() + "', missing @Produces annotation!");
    }

    public void register(Class<?> aClass, Class<? extends HttpResponseWriter> clazz) {

        Assert.notNull(clazz, "Missing response writer type class!");
        Assert.notNull(aClass, "Missing response writer type class!");

        log.info("Registering '" + aClass.getName() + "' writer '" + clazz.getName() + "'");
        super.register(aClass, clazz);
    }

    public void register(Class<?> aClass, HttpResponseWriter clazz) {

        Assert.notNull(clazz, "Missing response writer instance!");
        Assert.notNull(aClass, "Missing response writer type class!");

        log.info("Registering '" + aClass.getName() + "' writer '" + clazz.getClass().getName() + "'");
        super.register(aClass, clazz);
    }

    public void register(String mediaType, Class<? extends HttpResponseWriter> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + mediaType + "' writer '" + clazz.getName() + "'");
        super.register(mediaType, clazz);
    }

    public void register(String mediaType, HttpResponseWriter clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + mediaType + "' writer '" + clazz.getClass().getName() + "'");
        super.register(mediaType, clazz);
    }

    public void register(MediaType mediaType, Class<? extends HttpResponseWriter> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' writer '" + clazz.getName() + "'");
        super.register(mediaType, clazz);
    }

    public void register(MediaType mediaType, HttpResponseWriter clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' writer '" + clazz.getClass().getName() + "'");
        super.register(mediaType, clazz);
    }
}

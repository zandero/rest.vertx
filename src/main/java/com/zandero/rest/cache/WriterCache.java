package com.zandero.rest.cache;

import com.zandero.rest.data.*;
import com.zandero.rest.writer.*;
import com.zandero.utils.*;
import io.vertx.core.http.*;
import org.slf4j.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Provides definition and caching of response writer implementations
 */
public class WriterCache extends ClassCache<HttpResponseWriter> {

    private final static Logger log = LoggerFactory.getLogger(WriterCache.class);

    public WriterCache() {
        // default writers
        clear();
    }

    @Override
    public void clear() {

        super.clear();

        typeCache.put(javax.ws.rs.core.Response.class, JaxResponseWriter.class);
        typeCache.put(jakarta.ws.rs.core.Response.class, JakartaResponseWriter.class);

        typeCache.put(HttpServerResponse.class, VertxResponseWriter.class);

        mediaTypeCache.put("application/json", JsonResponseWriter.class);
        mediaTypeCache.put("text/html", GenericResponseWriter.class);

        // if not found ... default to simple toString() writer
        mediaTypeCache.put("text/plain", PlainResponseWriter.class);
        mediaTypeCache.put("*/*", PlainResponseWriter.class);
    }

    public void register(Class<? extends HttpResponseWriter> writer) {

        Assert.notNull(writer, "Missing writer type!");
        boolean registered = false;

        // TODO: to be removed
        Produces found = writer.getAnnotation(Produces.class);
        if (found != null) {
            MediaType[] produces = MediaTypeHelper.getMediaTypes(found.value());
            if (produces != null && produces.length > 0) {
                for (MediaType type : produces) {
                    super.registerInstanceByMediaType(type, writer);
                }
                registered = true;
            }
        }

        jakarta.ws.rs.Produces foundJakarta = writer.getAnnotation(jakarta.ws.rs.Produces.class);
        if (foundJakarta != null) {
            MediaType[] produces = MediaTypeHelper.getMediaTypes(foundJakarta.value());
            if (produces != null && produces.length > 0) {
                for (MediaType type : produces) {
                    super.registerInstanceByMediaType(type, writer);
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
                    super.registerInstanceByMediaType(type, writer); // TODO: might be register without super
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
        super.registerTypeByAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, HttpResponseWriter clazz) {

        Assert.notNull(clazz, "Missing response writer instance!");
        Assert.notNull(aClass, "Missing response writer type class!");

        log.info("Registering '" + aClass.getName() + "' writer '" + clazz.getClass().getName() + "'");
        super.registerInstanceByAssociatedType(aClass, clazz);
    }

    public void register(String mediaType, Class<? extends HttpResponseWriter> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + mediaType + "' writer '" + clazz.getName() + "'");
        super.registerTypeByMediaType(mediaType, clazz);
    }

    public void register(String mediaType, HttpResponseWriter clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + mediaType + "' writer '" + clazz.getClass().getName() + "'");
        super.registerInstanceByMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, Class<? extends HttpResponseWriter> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' writer '" + clazz.getName() + "'");
        super.registerInstanceByMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, HttpResponseWriter clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' writer '" + clazz.getClass().getName() + "'");
        super.registerInstanceByMediaType(mediaType, clazz);
    }
}

package com.zandero.rest.cache;

import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.provisioning.ClassFactory;
import com.zandero.rest.writer.*;
import com.zandero.utils.Assert;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.lang.reflect.Type;

import static com.zandero.rest.provisioning.ClassUtils.*;

/**
 * Provides definition and caching of response writer implementations
 */
public class WriterCache extends MediaTypesClassCache<HttpResponseWriter> {

    private final static Logger log = LoggerFactory.getLogger(WriterCache.class);

    public WriterCache() {
        clear();
    }

    @Override
    public void clear() {

        super.clear();

        associatedTypeMap.put(Response.class, JaxResponseWriter.class);
        associatedTypeMap.put(HttpServerResponse.class, VertxResponseWriter.class);

        associatedMediaTypeMap.put(MediaType.APPLICATION_JSON, JsonResponseWriter.class);
        associatedMediaTypeMap.put(MediaType.TEXT_HTML, GenericResponseWriter.class);

        // if not found ... default to simple toString() writer
        associatedMediaTypeMap.put(MediaType.TEXT_PLAIN, PlainResponseWriter.class);
        associatedMediaTypeMap.put(MediaType.WILDCARD, PlainResponseWriter.class);
    }

    public void register(Class<? extends HttpResponseWriter> writer) {

        Assert.notNull(writer, "Missing writer type!");
        boolean registered = false;

        Produces found = writer.getAnnotation(Produces.class);
        if (found != null) {
            MediaType[] produces = MediaTypeHelper.getMediaTypes(found.value());
            if (produces != null && produces.length > 0) {
                for (MediaType type : produces) {
                    super.registerAssociatedTypeByMediaType(type, writer);
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
                    super.registerInstanceByAssociatedMediaType(type, writer); // TODO: might be register without super
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
        super.registerAssociatedType(aClass, clazz);
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
        super.registerAssociatedTypeByMediaType(mediaType, clazz);
    }

    public void register(String mediaType, HttpResponseWriter clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + mediaType + "' writer '" + clazz.getClass().getName() + "'");
        super.registerInstanceByAssociatedMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, Class<? extends HttpResponseWriter> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' writer '" + clazz.getName() + "'");
        super.registerAssociatedTypeByMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, HttpResponseWriter clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing response writer!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' writer '" + clazz.getClass().getName() + "'");
        super.registerInstanceByAssociatedMediaType(mediaType, clazz);
    }
}

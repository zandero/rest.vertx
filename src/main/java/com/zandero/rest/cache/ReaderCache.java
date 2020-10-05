package com.zandero.rest.cache;

import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.reader.*;
import com.zandero.utils.Assert;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * Provides definition and caching of request body reader implementations
 */
public class ReaderCache extends ClassCache<ValueReader> {

    private final static Logger log = LoggerFactory.getLogger(ReaderCache.class);

    public ReaderCache() {
        clear();
    }

    @Override
    public void clear() {

        super.clear();

        typeCache.put(String.class, GenericValueReader.class);

        // pre fill with most generic implementation
        mediaTypeCache.put(MediaType.APPLICATION_JSON, JsonValueReader.class);
        mediaTypeCache.put(MediaType.TEXT_PLAIN, GenericValueReader.class);
    }

    public ValueReader get(MethodParameter parameter,
                           InjectionProvider provider,
                           RoutingContext context,
                           MediaType... mediaTypes) {

        return get(parameter, parameter.getReader(), provider, context, mediaTypes);
    }

    /**
     * Step over all possibilities to provide desired reader
     *
     * @param parameter          check parameter if reader is set or we have a type reader present
     * @param byMethodDefinition check default definition
     * @param provider           injection provider if any
     * @param context            routing context
     * @param mediaTypes         check by consumes annotation
     * @return found reader or GenericBodyReader
     */
    public ValueReader get(MethodParameter parameter,
                           Class<? extends ValueReader> byMethodDefinition,
                           InjectionProvider provider,
                           RoutingContext context,
                           MediaType... mediaTypes) {

        // by type
        Class<?> readerType = null;
        try {

            // reader parameter as given
            Assert.notNull(parameter, "Missing parameter!");
            Class<? extends ValueReader> reader = parameter.getReader();
            if (reader != null) {
                return (ValueReader) ClassFactory.getClassInstance(reader, this, provider, context);
            }

            // by value type, if body also by method/class definition or consumes media type
            readerType = parameter.getDataType();

            ValueReader valueReader = (ValueReader) ClassFactory.get(readerType, this, byMethodDefinition, provider, context, mediaTypes);
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
     * Takes media type from @Consumes annotation if specified, otherwise fallback to generics
     *
     * @param reader to be registered
     */
    public void register(Class<? extends ValueReader> reader) {

        Assert.notNull(reader, "Missing reader class!");
        boolean registered = false;

        Consumes found = reader.getAnnotation(Consumes.class);
        if (found != null) {
            MediaType[] consumes = MediaTypeHelper.getMediaTypes(found.value());
            if (consumes != null && consumes.length > 0) {
                for (MediaType type : consumes) {
                    register(type, reader);
                }
            }
            registered = true;
        }

        //register(reader.gereader);

        Assert.isTrue(registered,
                      "Failed to register reader: '" + reader.getName() + "', missing @Consumes annotation!");
    }

    /**
     * Registers both ... generic type and media type if given in @Consumes annotation
     */
    public void register(ValueReader reader) {

        Assert.notNull(reader, "Missing reader!");
        boolean registered = false;

        Consumes found = reader.getClass().getAnnotation(Consumes.class);
        if (found != null) {
            MediaType[] consumes = MediaTypeHelper.getMediaTypes(found.value());
            if (consumes != null && consumes.length > 0) {
                for (MediaType type : consumes) {
                    register(type, reader);
                }
                registered = true;
            }
        }

        Assert.isTrue(registered,
                      "Failed to register reader: '" + reader.getClass().getName() + "', missing @Consumes annotation!");
    }

    public void register(Class<?> clazz, Class<? extends ValueReader> reader) {

        Assert.notNull(clazz, "Missing reader type class!");
        Assert.notNull(reader, "Missing request reader type class!");

        log.info("Registering '" + clazz.getName() + "' reader '" + reader.getName() + "'");
        super.registerTypeByAssociatedType(clazz, reader);
    }

    public void register(Class<?> clazz, ValueReader reader) {

        Assert.notNull(clazz, "Missing reader type class!");
        Assert.notNull(reader, "Missing request reader!");

        log.info("Registering '" + clazz.getName() + "' reader '" + reader.getClass().getName() + "'");
        super.registerInstanceByAssociatedType(clazz, reader);
    }

    public void register(String mediaType, Class<? extends ValueReader> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + mediaType + "' reader '" + clazz.getName() + "'");
        super.registerTypeByMediaType(mediaType, clazz);
    }

    public void register(String mediaType, ValueReader clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + mediaType + "' reader '" + clazz.getClass().getName() + "'");
        super.registerInstanceByMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, Class<? extends ValueReader> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' reader '" + clazz.getName() + "'");
        super.registerInstanceByMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, ValueReader clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' reader '" + clazz.getClass().getName() + "'");
        super.registerInstanceByMediaType(mediaType, clazz);
    }
}

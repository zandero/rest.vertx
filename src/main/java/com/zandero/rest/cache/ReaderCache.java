package com.zandero.rest.cache;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.reader.*;
import com.zandero.utils.Assert;
import org.slf4j.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * Provides definition and caching of request body reader implementations
 */
public class ReaderCache extends MediaTypesClassCache<ValueReader> {

    private final static Logger log = LoggerFactory.getLogger(ReaderCache.class);

    public ReaderCache() {
        clear();
    }

    @Override
    public void clear() {

        super.clear();

        associatedTypeMap.put(String.class, GenericValueReader.class);

        // pre fill with most generic implementation
        associatedMediaTypeMap.put(MediaType.APPLICATION_JSON, JsonValueReader.class);
        associatedMediaTypeMap.put(MediaType.TEXT_PLAIN, GenericValueReader.class);
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
                      "Failed to register reader: '" + reader.getClass().getName() +
                          "', missing @Consumes annotation!");
    }

    public void register(Class<?> clazz, Class<? extends ValueReader> reader) {

        Assert.notNull(clazz, "Missing reader type class!");
        Assert.notNull(reader, "Missing request reader type class!");

        log.info("Registering '" + clazz.getName() + "' reader '" + reader.getName() + "'");
        super.registerAssociatedType(clazz, reader);
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
        super.registerAssociatedTypeByMediaType(mediaType, clazz);
    }

    public void register(String mediaType, ValueReader clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + mediaType + "' reader '" + clazz.getClass().getName() + "'");
        super.registerInstanceByAssociatedMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, Class<? extends ValueReader> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' reader '" + clazz.getName() + "'");
        super.registerAssociatedTypeByMediaType(mediaType, clazz);
    }

    public void register(MediaType mediaType, ValueReader clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing value reader!");

        log.info("Registering '" + MediaTypeHelper.toString(mediaType) + "' reader '" + clazz.getClass().getName() + "'");
        super.registerInstanceByAssociatedMediaType(mediaType, clazz);
    }
}

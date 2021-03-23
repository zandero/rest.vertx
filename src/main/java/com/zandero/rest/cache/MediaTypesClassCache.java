package com.zandero.rest.cache;

import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.provisioning.ClassProducer;
import com.zandero.utils.Assert;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.MediaType;
import java.util.*;

public abstract class MediaTypesClassCache<T> extends ClassCache<T> {

    /**
     * map of media type associated with class type (to be instantiated)
     * Map of mediaType(X) -> class(A)->name
     */
    protected Map<String, Class<? extends T>> associatedMediaTypeMap = new LinkedHashMap<>();

    @Override
    public void clear() {
        super.clear();
        associatedMediaTypeMap.clear();
    }

    /**
     * @param mediaType to find associated class type for
     * @return found class type or null if none found
     */
    public Class<? extends T> getAssociatedTypeFromMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }

        return associatedMediaTypeMap.get(MediaTypeHelper.getKey(mediaType));
    }

    /**
     * Tries to find registered class by associated type
     *
     * @param mediaType associated media type
     * @return found class instance or null if not found
     */
    public T getInstanceByAssociatedMediaType(MediaType mediaType) {
        Class<? extends T> found = getAssociatedTypeFromMediaType(mediaType);
        if (found != null) { // is registered ... try finding instance
            return getInstanceByType(found);
        }

        return null;
    }


    public void registerInstanceByAssociatedMediaType(MediaType mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        String key = MediaTypeHelper.getKey(mediaType);
        associatedMediaTypeMap.put(key, (Class<? extends T>) clazz.getClass());
        registerInstance(clazz);
    }

    public void registerInstanceByAssociatedMediaType(String mediaType, T clazz) {

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        registerInstanceByAssociatedMediaType(type, clazz);
    }

    protected void registerAssociatedTypeByMediaType(MediaType mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class!");

        String key = MediaTypeHelper.getKey(mediaType);
        associatedMediaTypeMap.put(key, clazz);
    }

    protected void registerAssociatedTypeByMediaType(String mediaType, Class<? extends T> clazz) {

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        registerAssociatedTypeByMediaType(type, clazz);
    }

    @Deprecated
    public T get(Class<?> type,
                 Class<?> byDefinition,
                 InjectionProvider provider,
                 RoutingContext routeContext,
                 MediaType[] mediaTypes) throws ClassFactoryException, ContextException {

        Class<?> clazz = byDefinition;

        // No class defined ... try by type
        if (clazz == null) {
            T writer = getInstanceByAssociatedType(type);
            if (writer != null) {
                return writer;
            }

            clazz = getAssociatedType(type);
        }

        // try with media type ...
        if (clazz == null && mediaTypes != null && mediaTypes.length > 0) {

            for (MediaType mediaType : mediaTypes) {
                T writer = getInstanceByAssociatedMediaType(mediaType);
                if (writer != null) {
                    return writer;
                }

                clazz = getAssociatedTypeFromMediaType(mediaType);
                if (clazz != null) {
                    break;
                }
            }
        }

        return (T) ClassProducer.getClassInstance(clazz, this, provider, routeContext);
    }
}

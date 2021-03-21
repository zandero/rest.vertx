package com.zandero.rest.cache;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.utils.Assert;

import javax.ws.rs.core.MediaType;
import java.util.*;

public abstract class MediaTypesClassCache<T> extends ClassCache<T> {

    /**
     * map of media type associated with class type (to be instantiated)
     */
    protected Map<String, Class<? extends T>> mediaTypeCache = new LinkedHashMap<>();

    @Override
    public void clear() {
        super.clear();
        mediaTypeCache.clear();
    }

    public Class<? extends T> getInstanceFromMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }

        return mediaTypeCache.get(MediaTypeHelper.getKey(mediaType));
    }

    protected void registerInstanceByMediaType(MediaType mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        String key = MediaTypeHelper.getKey(mediaType);
        instanceCache.put(key, clazz);
    }

    protected void registerInstanceByMediaType(String mediaType, T clazz) {

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        registerInstanceByMediaType(type, clazz);
    }
    
    protected void registerTypeByMediaType(MediaType mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class!");

        String key = MediaTypeHelper.getKey(mediaType);
        mediaTypeCache.put(key, clazz);
    }

    protected void registerTypeByMediaType(String mediaType, Class<? extends T> clazz) {

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        registerTypeByMediaType(type, clazz);
    }
}

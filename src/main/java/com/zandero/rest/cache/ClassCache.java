package com.zandero.rest.cache;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.utils.Assert;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.*;

import static com.zandero.rest.data.ClassUtils.*;

/**
 * Base class to cache class instances by name, type, media type ...
 */
public abstract class ClassCache<T> {

    /**
     * Cache of class instances
     */
    protected final Map<String, T> instanceCache = new HashMap<>();

    /**
     * map of class associated with class type (to be instantiated)
     */
    protected Map<Class<?>, Class<? extends T>> typeCache = new LinkedHashMap<>();

    /**
     * map of media type associated with class type (to be instantiated)
     */
    protected Map<String, Class<? extends T>> mediaTypeCache = new LinkedHashMap<>();

    public void clear() {
        instanceCache.clear();
        typeCache.clear();
        mediaTypeCache.clear();
    }

    public T getInstanceByName(String name) {
        return instanceCache.get(name);
    }

    public T getInstanceByType(Class<? extends T> clazz) {
        return instanceCache.get(clazz.getName());
    }

    public Class<? extends T> getInstanceFromType(Class<?> type) {
        if (type == null) {
            return null;
        }
        // try to find appropriate class if mapped (by type)
        for (Class<?> key : typeCache.keySet()) {
            if (key.isInstance(type) || key.isAssignableFrom(type)) {
                return typeCache.get(key);
            }
        }

        return null;
    }

    public Class<? extends T> getInstanceFromMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }

        return mediaTypeCache.get(MediaTypeHelper.getKey(mediaType));
    }

    public void registerInstance(T clazz) {
        Assert.notNull(clazz, "Missing class instance!");
        instanceCache.put(clazz.getClass().getName(), clazz);
    }

    protected void registerInstanceByMediaType(String mediaType, T clazz) {

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        registerInstanceByMediaType(type, clazz);
    }

    protected void registerInstanceByMediaType(MediaType mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        String key = MediaTypeHelper.getKey(mediaType);
        instanceCache.put(key, clazz);
    }

    protected void registerTypeByMediaType(String mediaType, Class<? extends T> clazz) {

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        registerInstanceByMediaType(type, clazz);
    }

    protected void registerInstanceByMediaType(MediaType mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class!");

        String key = MediaTypeHelper.getKey(mediaType);
        mediaTypeCache.put(key, clazz);
    }

    protected void registerTypeByAssociatedType(Class<?> aClass, Class<? extends T> clazz) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(clazz, "Missing response type class!");

        if (checkCompatibility(clazz)) {
            Type expected = getGenericType(clazz);
            checkIfCompatibleType(aClass, expected, "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + clazz + "'!");
        }

        typeCache.put(aClass, clazz);
    }

    protected void registerInstanceByAssociatedType(Class<?> aClass, T instance) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(instance, "Missing instance of class!");

        if (checkCompatibility(instance.getClass())) {
            Type expected = getGenericType(instance.getClass());
            checkIfCompatibleType(aClass,
                                  expected,
                                  "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + instance.getClass() + "'!");
        }

        instanceCache.put(aClass.getName(), instance);
    }
}

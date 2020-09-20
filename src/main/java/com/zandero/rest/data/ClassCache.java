package com.zandero.rest.data;

import com.zandero.utils.Assert;
import org.slf4j.*;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.*;

import static com.zandero.rest.data.ClassUtils.*;

/**
 * Caching of classes
 */
public abstract class ClassCache<T> {

    private final static Logger log = LoggerFactory.getLogger(ClassCache.class);

    /**
     * Cache of class instances
     */
    protected final Map<String, T> cache = new HashMap<>();

    /**
     * map of class associated with class type (to be instantiated)
     */
    public Map<Class<?>, Class<? extends T>> classTypes = new LinkedHashMap<>();

    /**
     * map of media type associated with class type (to be instantiated)
     */
    public Map<String, Class<? extends T>> mediaTypes = new LinkedHashMap<>();

    public void clear() {

        // clears caches
        classTypes.clear();
        mediaTypes.clear();
        cache.clear();

        setDefaults();
    }

    protected abstract void setDefaults();

    public void cache(T instance) {

        cache.put(instance.getClass().getName(), instance);
    }

    public T getCached(Class<? extends T> clazz) {

        return cache.get(clazz.getName());
    }

    public Class<? extends T> get(Class<?> type) {

        if (type == null) {
            return null;
        }
        // try to find appropriate class if mapped (by type)
        for (Class<?> key : classTypes.keySet()) {
            if (key.isInstance(type) || key.isAssignableFrom(type)) {
                return classTypes.get(key);
            }
        }

        return null;
    }

    public Class<? extends T> get(MediaType mediaType) {

        if (mediaType == null) {
            return null;
        }

        return mediaTypes.get(MediaTypeHelper.getKey(mediaType));
    }

    public T get(String name) {
        return cache.get(name);
    }


    protected void register(String mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class");

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        Assert.notNull(type, "Unknown media type given: " + mediaType + "!");

        String key = MediaTypeHelper.getKey(type);
        mediaTypes.put(key, clazz);
    }

    protected void register(String mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        Assert.notNull(type, "Unknown media type given: " + mediaType + "!");

        String key = MediaTypeHelper.getKey(type);
        cache.put(key, clazz);
    }

    protected void register(MediaType mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class!");

        String key = MediaTypeHelper.getKey(mediaType);
        mediaTypes.put(key, clazz);
    }

    protected void register(MediaType mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        String key = MediaTypeHelper.getKey(mediaType);
        cache.put(key, clazz);
    }

    protected void register(T clazz) {

        Assert.notNull(clazz, "Missing class instance!");
        cache.put(clazz.getClass().getName(), clazz);
    }

    protected void register(Class<?> aClass, Class<? extends T> clazz) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(clazz, "Missing response type class!");

        if (checkCompatibility(clazz)) {
            Type expected = getGenericType(clazz);
            checkIfCompatibleType(aClass, expected, "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + clazz + "'!");
        }

        classTypes.put(aClass, clazz);
    }

    protected void register(Class<?> aClass, T instance) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(instance, "Missing instance of class!");

        if (checkCompatibility(instance.getClass())) {
            Type expected = getGenericType(instance.getClass());
            checkIfCompatibleType(aClass,
                                  expected,
                                  "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + instance.getClass() + "'!");
        }

        cache.put(aClass.getName(), instance);
    }
}

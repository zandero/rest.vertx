package com.zandero.rest.data;

import com.zandero.rest.exception.ExecuteException;
import com.zandero.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple class instance cache
 */
public abstract class ClassFactory<T> {

	private final static Logger log = LoggerFactory.getLogger(ClassFactory.class);

	private Map<String, T> cache = new HashMap<>();

	protected Map<String, Class<? extends T>> classTypes = new HashMap<>();

	protected Map<String, Class<? extends T>> mediaTypes = new HashMap<>();

	public ClassFactory() {
		init();
	}

	abstract protected void init();

	public void clear() {

		// clears any additionally registered writers and initializes defaults
		classTypes.clear();
		mediaTypes.clear();

		init();
	}

	private void cache(T instance) {

		cache.put(instance.getClass().getName(), instance);
	}

	private T getCached(Class<? extends T> reader) {

		return cache.get(reader.getName());
	}

	protected T getClassInstance(Class<? extends T> writer) {

		if (writer != null) {
			try {

				T instance = getCached(writer);
				if (instance == null) {

					instance = writer.newInstance();
					cache(instance);
				}

				return instance;
			}
			catch (InstantiationException | IllegalAccessException e) {
				log.error("Failed to instantiate response writer '" + writer.getName() + "' " + e.getMessage(), e);
				// TODO: probably best to throw exception here
			}
		}

		return null;
	}

	public void register(String mediaType, Class<? extends T> clazz) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(clazz, "Missing media type class");

		MediaType type = MediaType.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		String key = MediaTypeHelper.getKey(type);
		mediaTypes.put(key, clazz);
	}

	public void register(MediaType mediaType, Class<? extends T> clazz) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(clazz, "Missing media type class");

		String key = MediaTypeHelper.getKey(mediaType);
		mediaTypes.put(key, clazz);
	}

	public void register(Class<?> response, Class<? extends T> clazz) {

		Assert.notNull(response, "Missing response class!");
		Assert.notNull(clazz, "Missing response type class");

		classTypes.put(response.getName(), clazz);
	}

	public T get(Class<?> returnType, Class<? extends T> byDefinition, MediaType[] mediaTypes) throws ExecuteException {

		Class<? extends T> reader = byDefinition;

		// 2. if no writer is specified ... try to find appropriate writer by response type
		if (reader == null) {

			if (returnType != null) {
				// try to find appropriate writer if mapped
				reader = classTypes.get(returnType.getName());
			}
		}

		if (reader == null) { // try by consumes

			if (mediaTypes != null && mediaTypes.length > 0) {

				for (MediaType type : mediaTypes) {
					reader = get(type);
					if (reader != null) {
						break;
					}
				}
			}
		}

		if (reader != null) {
			return getClassInstance(reader);
		}

		return null;
	}

	private Class<? extends T> get(MediaType mediaType) {

		if (mediaType == null) {
			return null;
		}

		return mediaTypes.get(MediaTypeHelper.getKey(mediaType));
	}

	public T get(String mediaType) {

		Class<? extends T> clazz = get(MediaType.valueOf(mediaType));
		return getClassInstance(clazz);
	}
}

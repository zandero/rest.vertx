package com.zandero.rest.data;

import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.reader.GenericBodyReader;
import com.zandero.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple class instance cache
 */
public abstract class ClassFactory<T> {

	private final static Logger log = LoggerFactory.getLogger(ClassFactory.class);

	private Map<String, T> cache = new HashMap<>();

	protected Map<Class, Class<? extends T>> classTypes = new LinkedHashMap<>();

	protected Map<String, Class<? extends T>> mediaTypes = new LinkedHashMap<>();

	public ClassFactory() {
		init();
	}

	abstract protected void init();

	public void clear() {

		// clears any additionally registered writers and initializes defaults
		classTypes.clear();
		mediaTypes.clear();
		cache.clear();

		init();
	}

	private void cache(T instance) {

		cache.put(instance.getClass().getName(), instance);
	}

	private T getCached(Class<? extends T> reader) {

		return cache.get(reader.getName());
	}

	protected T getClassInstance(Class<? extends T> clazz) throws ClassFactoryException {

		if (clazz == null) {
			return null;
		}

		try {

			T instance = getCached(clazz);
			if (instance == null) {

				instance = clazz.newInstance();
				cache(instance);
			}

			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Failed to instantiate class '" + clazz.getName() + "' " + e.getMessage(), e);
			throw new ClassFactoryException("Failed to instantiate class of type: " + clazz.getName() + ", class needs empty constructor!", e);
		}
	}

	public static Object newInstanceOf(Class<?> clazz) throws ClassFactoryException {

		if (clazz == null) {
			return null;
		}

		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Failed to instantiate class '" + clazz.getName() + "' " + e.getMessage(), e);
			throw new ClassFactoryException("Failed to instantiate class of type: " + clazz.getName() + ", class needs empty constructor!", e);
		}
	}

	protected void register(String mediaType, Class<? extends T> clazz) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(clazz, "Missing media type class");

		MediaType type = MediaType.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		String key = MediaTypeHelper.getKey(type);
		mediaTypes.put(key, clazz);
	}

	protected void register(MediaType mediaType, Class<? extends T> clazz) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(clazz, "Missing media type class");

		String key = MediaTypeHelper.getKey(mediaType);
		mediaTypes.put(key, clazz);
	}

	protected void register(Class<?> aClass, Class<? extends T> clazz) {

		Assert.notNull(aClass, "Missing associated class!");
		Assert.notNull(clazz, "Missing response type class");

		Type expected = getGenericType(clazz);
		checkIfCompatibleTypes(aClass, expected, "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + clazz + "'");

		classTypes.put(aClass, clazz);
	}

	protected T get(Class<?> type, Class<? extends T> byDefinition, MediaType[] mediaTypes) throws ClassFactoryException {

		Class<? extends T> clazz = byDefinition;

		// 2. if no writer is specified ... try to find appropriate writer by response type
		if (clazz == null) {

			clazz = get(type);
			/*if (type != null) {
				// try to find appropriate class if mapped (by exact type)
				clazz = classTypes.get(type);

				// exact match failed ... go over keys and check if classes are related (inherited from ...)
				if (clazz == null) {
					for (Class key : classTypes.keySet()) {
						if (key.isInstance(type) || key.isAssignableFrom(type)) {
							clazz = classTypes.get(key);
							break;
						}
					}
				}
			}*/
		}

		// try by consumes annotation
		if (clazz == null) {

			if (mediaTypes != null && mediaTypes.length > 0) {

				for (MediaType mediaType : mediaTypes) {
					clazz = get(mediaType);

					if (clazz != null) {
						break;
					}
				}
			}
		}

		if (clazz != null) {
			return getClassInstance(clazz);
		}

		return null;
	}

	private Class<? extends T> get(MediaType mediaType) {

		if (mediaType == null) {
			return null;
		}

		return mediaTypes.get(MediaTypeHelper.getKey(mediaType));
	}

	public T get(String mediaType) throws ClassFactoryException {

		Class<? extends T> clazz = get(MediaType.valueOf(mediaType));
		return getClassInstance(clazz);
	}

	public Class<? extends T> get(Class<?> type) {

		if (type == null) {
			return null;
		}
		// try to find appropriate class if mapped (by type)
		for (Class key : classTypes.keySet()) {
			if (key.isInstance(type) || key.isAssignableFrom(type)) {
				return classTypes.get(key);
			}
		}

		return null;
	}

	public static Type getGenericType(Class clazz) {

		Assert.notNull(clazz, "Missing class!");
		Type[] genericInterfaces = clazz.getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {

			if (genericInterface instanceof ParameterizedType) {

				Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
				return genericTypes[0];
			}
		}

		return null;
	}

	public static void checkIfCompatibleTypes(Class<?> expected, Type actual, String message) {

		boolean compatibleTypes = checkIfCompatibleTypes(expected, actual);
		Assert.isTrue(compatibleTypes, message);
	}

	@SuppressWarnings("unchecked")
	protected static boolean checkIfCompatibleTypes(Class<?> expected, Type actual) {

		if (actual == null) {
			return true;
		}

		if (actual instanceof ParameterizedType) {
			return expected.isAssignableFrom(((ParameterizedTypeImpl) actual).getRawType());
		} else if (actual instanceof TypeVariableImpl) { // we don't know at this point ... generic type
			return true;
		} else {
			return expected.equals(actual) || expected.isInstance(actual) || ((Class) actual).isAssignableFrom(expected);
		}
	}

	/**
	 * Aims to custruct given type utilizing a constructor that takes String or other primitive type values
	 *
	 * @param type         to be constructed
	 * @param defaultValue constructor param
	 * @return class object
	 * @throws ClassFactoryException in case type could not be constructed
	 */
	public static Object constructType(Class<?> type, String defaultValue) throws ClassFactoryException {

		Assert.notNull(type, "Missing type!");
		Assert.notNullOrEmptyTrimmed(defaultValue, "Missing default value!");

		Class[] primitiveTypes = new Class[]{
				String.class,
				int.class, Integer.class,
				boolean.class, Boolean.class,
				byte.class, Byte.class,
				char.class, Character.class,
				short.class, Short.class,
				long.class, Long.class,
				float.class, Float.class,
				double.class, Double.class
		};

		Constructor[] allConstructors = type.getDeclaredConstructors();
		for (Constructor ctor : allConstructors) {

			Class<?>[] pType = ctor.getParameterTypes();
			if (pType.length == 1) { // ok ... match ... might be possible to use

				try {

					for (Class primitive : primitiveTypes) {

						if (pType[0].isAssignableFrom(primitive)) {

							Object value = GenericBodyReader.stringToPrimitiveType(defaultValue, primitive);
							return ctor.newInstance(value);
						}
					}
				} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
					//continue; // try next one ... if any
					log.warn("Failed constructing: " + ctor, e);
				}
			}
		}

		throw new ClassFactoryException("Could not construct: " + type + " with default value: '" + defaultValue + "', " +
				                                "must provide String only or primitive type constructor!", null);
	}
}

package com.zandero.rest.data;

import com.zandero.rest.annotation.SuppressCheck;
import com.zandero.rest.context.ContextProviderFactory;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ContextException;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.Assert;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.*;
import java.util.*;

/**
 * Simple class instance cache and class factory utility
 */
public abstract class ClassFactory<T> {

	private final static Logger log = LoggerFactory.getLogger(ClassFactory.class);

	/**
	 * Cache of class instances
	 */
	private Map<String, T> cache = new HashMap<>();

	/**
	 * map of class associated with class type (to be instantiated)
	 */
	protected Map<Class, Class<? extends T>> classTypes = new LinkedHashMap<>();

	/**
	 * map of media type associated with class type (to be instantiated)
	 */
	protected Map<String, Class<? extends T>> mediaTypes = new LinkedHashMap<>();

	private static Class[] SIMPLE_TYPE = new Class[]{
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

	public ClassFactory() {

		init();
	}

	abstract protected void init();

	public void clear() {

		// clears caches
		classTypes.clear();
		mediaTypes.clear();
		cache.clear();

		init();
	}

	private void cache(T instance) {

		cache.put(instance.getClass().getName(), instance);
	}

	private T getCached(Class<? extends T> clazz) {

		return cache.get(clazz.getName());
	}

	/*protected T getCached(String key) {

		return cache.get(key);
	}*/

	@SuppressWarnings("unchecked")
	public T getClassInstance(Class<? extends T> clazz,
	                          InjectionProvider provider,
	                          RoutingContext context) throws ClassFactoryException,
	                                                         ContextException {

		if (clazz == null) {
			return null;
		}

		// only use cache if no @Context is needed
		boolean hasContext = ContextProviderFactory.hasContext(clazz);

		T instance = null;
		if (!hasContext) { // no Context ... we can get it from cache
			instance = getCached(clazz);
		}
		if (instance == null) {

			instance = (T) newInstanceOf(clazz, provider, context);

			if (!hasContext) { // no context .. we can cache this instance
				cache(instance);
			}
		}

		return instance;
	}

	public static Object newInstanceOf(Class<?> clazz,
	                                   InjectionProvider provider,
	                                   RoutingContext context) throws ClassFactoryException, ContextException {

		if (clazz == null) {
			return null;
		}

		Object instance;

		if (provider == null || !InjectionProvider.hasInjection(clazz)) {
			instance = newInstanceOf(clazz);
		} else {

			try {
				instance = provider.getInstance(clazz);
				if (instance == null) {
					throw new ClassFactoryException("Failed to getInstance class of type: " + clazz.getName() + ", with injector: " +
					                                provider.getClass().getName() + "!", null);
				}
			}
			catch (Throwable e) {
				throw new ClassFactoryException("Failed to getInstance class of type: " + clazz.getName() + ", with injector: " +
				                                provider.getClass().getName() + "!", e);
			}

		}

		if (ContextProviderFactory.hasContext(clazz)) {
			ContextProviderFactory.injectContext(instance, context);
		}

		return instance;
	}

	public static Object newInstanceOf(Class<?> clazz) throws ClassFactoryException {

		if (clazz == null) {
			return null;
		}

		try {

			for (Constructor<?> c : clazz.getDeclaredConstructors()) {
				c.setAccessible(true);
				// initialize with empty constructor
				if (c.getParameterCount() == 0) { // TODO: try to initialize class from context if arguments fit
					return c.newInstance();
				}
			}
		}
		catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
			log.error("Failed to instantiate class '" + clazz.getName() + "' " + e.getMessage(), e);
			throw new ClassFactoryException("Failed to instantiate class of type: " + clazz.getName() + ", class needs empty constructor!", e);
		}

		throw new ClassFactoryException("Failed to instantiate class of type: " + clazz.getName() + ", class needs empty constructor!", null);
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
			checkIfCompatibleTypes(aClass, expected, "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + clazz + "'!");
		}

		classTypes.put(aClass, clazz);
	}


	/**
	 * checks if @SuppressCheck annotation is given
	 *
	 * @param clazz to inspect
	 * @return true if compatible, false otherwise
	 */
	public static boolean checkCompatibility(Class<?> clazz) {

		return clazz != null && clazz.getAnnotation(SuppressCheck.class) == null;
	}

	protected void register(Class<?> aClass, T instance) {

		Assert.notNull(aClass, "Missing associated class!");
		Assert.notNull(instance, "Missing instance of class!");

		if (checkCompatibility(instance.getClass())) {
			Type expected = getGenericType(instance.getClass());
			checkIfCompatibleTypes(aClass,
			                       expected,
			                       "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + instance.getClass() + "'!");
		}

		cache.put(aClass.getName(), instance);
	}

	// TODO : move media type specific into a new class that Reader, Writer factory derives from
	protected T get(Class<?> type,
	                Class<? extends T> byDefinition,
	                InjectionProvider provider,
	                RoutingContext routeContext,
	                MediaType[] mediaTypes) throws ClassFactoryException,
	                                               ContextException {

		Class<? extends T> clazz = byDefinition;

		// No class defined ... try by type
		if (clazz == null) {
			clazz = get(type);
		}

		// try with media type ...
		if (clazz == null && mediaTypes != null && mediaTypes.length > 0) {

			for (MediaType mediaType : mediaTypes) {
				clazz = get(mediaType);

				if (clazz != null) {
					break;
				}
			}
		}

		if (clazz != null) {
			return getClassInstance(clazz, provider, routeContext);
		}

		// 3. find cached instance ... if any
		return cache.get(type.getName());
	}

	private Class<? extends T> get(MediaType mediaType) {

		if (mediaType == null) {
			return null;
		}

		return mediaTypes.get(MediaTypeHelper.getKey(mediaType));
	}

	public T get(String mediaType, RoutingContext routeContext) throws ClassFactoryException,
	                                                                   ContextException {

		Class<? extends T> clazz = get(MediaTypeHelper.valueOf(mediaType));
		return getClassInstance(clazz, null, routeContext);
	}

	@SuppressWarnings("unchecked")
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
		}

		if (actual instanceof TypeVariableImpl) { // we don't know at this point ... generic type
			return true;
		}

		return expected.equals(actual) || expected.isInstance(actual) || ((Class) actual).isAssignableFrom(expected);
	}

	static Object stringToPrimitiveType(String value, Class<?> dataType) {

		if (value == null) {
			return null;
		}

		if (dataType.equals(String.class)) {
			return value;
		}

		// primitive types need to be cast differently
		if (dataType.isAssignableFrom(boolean.class) || dataType.isAssignableFrom(Boolean.class)) {
			return Boolean.valueOf(value);
		}

		if (dataType.isAssignableFrom(byte.class) || dataType.isAssignableFrom(Byte.class)) {
			return Byte.valueOf(value);
		}

		if (dataType.isAssignableFrom(char.class) || dataType.isAssignableFrom(Character.class)) {

			Assert.isTrue(value.length() != 0, "Expected Character but got: null");
			return value.charAt(0);
		}

		if (dataType.isAssignableFrom(short.class) || dataType.isAssignableFrom(Short.class)) {
			return Short.valueOf(value);
		}

		if (dataType.isAssignableFrom(int.class) || dataType.isAssignableFrom(Integer.class)) {
			return Integer.valueOf(value);
		}

		if (dataType.isAssignableFrom(long.class) || dataType.isAssignableFrom(Long.class)) {
			return Long.valueOf(value);
		}

		if (dataType.isAssignableFrom(float.class) || dataType.isAssignableFrom(Float.class)) {
			return Float.valueOf(value);
		}

		if (dataType.isAssignableFrom(double.class) || dataType.isAssignableFrom(Double.class)) {
			return Double.valueOf(value);
		}

		return null;
	}

	/**
	 * Aims to construct given type utilizing a constructor that takes String or other primitive type values
	 *
	 * @param type      to be constructed
	 * @param fromValue constructor param
	 * @param <T>       type of value
	 * @return class object
	 * @throws ClassFactoryException in case type could not be constructed
	 */
	public static <T> Object constructType(Class<T> type, String fromValue) throws ClassFactoryException {

		Assert.notNull(type, "Missing type!");

		// a primitive or "simple" type
		if (isSimpleType(type)) {
			return stringToPrimitiveType(fromValue, type);
		}

		// have a constructor that accepts a single argument (String or any other primitive type that can be converted from String)
		Object result = constructViaConstructor(type, fromValue);
		if (result != null) {
			return result;
		}

		result = constructViaMethod(type, fromValue);
		if (result != null) {
			return result;
		}

		//
		// have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.
		//

		// Be List, Set or SortedSet, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.
		/*if (type.isAssignableFrom(List.class) ||
			type.isAssignableFrom(Set.class) ||
			type.isAssignableFrom(SortedSet.class))*/


		throw new ClassFactoryException("Could not construct: " + type + " with default value: '" + fromValue + "', " +
		                                "must provide String only or primitive type constructor, " +
		                                "static fromString() or valueOf() methods!", null);
	}

	private static <T> boolean isSimpleType(Class<T> type) {

		for (Class primitive : SIMPLE_TYPE) {
			if (type.isAssignableFrom(primitive)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * have a constructor that accepts a single argument
	 * (String or any other primitive type that can be converted from String)
	 */
	static <T> Object constructViaConstructor(Class<T> type, String fromValue) {

		Constructor[] allConstructors = type.getDeclaredConstructors();
		for (Constructor ctor : allConstructors) {

			Class<?>[] pType = ctor.getParameterTypes();
			if (pType.length == 1) { // ok ... match ... might be possible to use

				try {

					for (Class primitive : SIMPLE_TYPE) {

						if (pType[0].isAssignableFrom(primitive)) {

							Object value = stringToPrimitiveType(fromValue, primitive);
							return ctor.newInstance(value);
						}
					}
				}
				catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
					//continue; // try next one ... if any
					log.warn("Failed constructing: " + ctor, e);
				}
			}
		}

		return null;
	}

	/**
	 * Constructs type via static method fromString(String value) or valueOf(String value)
	 *
	 * @param type      to be constructed
	 * @param fromValue value to take
	 * @param <T>       class type
	 * @return Object of type or null if failed to construct
	 */
	static <T> Object constructViaMethod(Class<T> type, String fromValue) {

		// Try to usse fromString before valueOf (enums have valueOf already defined) - in case we override fromString()
		List<Method> methods = getMethods(type, "fromString", "valueOf");

		if (methods != null && methods.size() > 0) {

			for (Method method : methods) {

				try {
					return method.invoke(null, fromValue);
				}
				catch (IllegalAccessException | InvocationTargetException e) {
					// failed with this one ... try the others ...
					log.warn("Failed invoking static method: " + method.getName());
				}
			}
		}

		return null;
	}

	/**
	 * Get methods in order desired to invoke them one by one until match or fail
	 *
	 * @param type  desired
	 * @param names of methods in order
	 * @param <T>   class to search for methods
	 * @return method list to use
	 */
	private static <T> List<Method> getMethods(Class<T> type, String... names) {

		Assert.notNull(type, "Missing class to search for methods!");
		Assert.notNullOrEmpty(names, "Missing method names to search for!");

		Map<String, Method> candidates = new HashMap<>();
		for (Method method : type.getMethods()) {

			if (Modifier.isStatic(method.getModifiers()) &&
			    method.getReturnType().equals(type) &&
			    method.getParameterTypes().length == 1 &&
			    method.getParameterTypes()[0].isAssignableFrom(String.class)) {

				candidates.put(method.getName(), method);
			}
		}

		List<Method> output = new ArrayList<>();

		// go in order desired
		for (String name : names) {
			if (candidates.containsKey(name)) {
				output.add(candidates.get(name));
			}
		}

		return output;
	}
}

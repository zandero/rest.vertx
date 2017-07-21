package com.zandero.rest.context;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.utils.Assert;

import java.util.LinkedHashMap;

/**
 * Storage of context providers
 */
public class ContextProviders {

	private LinkedHashMap<Class, ContextProvider> providers = new LinkedHashMap<>();

	/**
	 * Adds a class context provider
	 * @param aClass type to be provided
	 * @param provider to provide the type
	 */
	public void register(Class<?> aClass, ContextProvider provider) {

		Assert.notNull(aClass, "Missing provider class type!");
		Assert.notNull(provider, "Missing provider!");

		Assert.isFalse(providers.containsKey(aClass), "Provider: " + aClass.getName() + " already registered!");
		providers.put(aClass, provider);
	}

	/**
	 * Adds a class context provider
	 * @param aClass type to be provided
	 * @param provider to provide the type
	 */
	public void register(Class<?> aClass, Class<? extends ContextProvider> provider) {

		try {
			ContextProvider instance = (ContextProvider) ClassFactory.newInstanceOf(provider);
			register(aClass, instance);
		} catch (ClassFactoryException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public ContextProvider get(Class clazz) {
		return providers.get(clazz);
	}

	public void clear() {
		providers.clear();
	}
}

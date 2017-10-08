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
	 * @param clazz type to be provided
	 * @param provider to provide the type
	 */
	public void register(Class<?> clazz, ContextProvider provider) {

		Assert.notNull(clazz, "Missing provider class type!");
		Assert.notNull(provider, "Missing context provider!");

		Assert.isFalse(providers.containsKey(clazz), "Provider: " + clazz.getName() + " already registered!");
		providers.put(clazz, provider);
	}

	/**
	 * Adds a class context provider
	 * @param clazz type to be provided
	 * @param provider to provide the type
	 */
	public void register(Class<?> clazz, Class<? extends ContextProvider> provider) {

		Assert.notNull(clazz, "Missing provider class type!");
		Assert.notNull(provider, "Missing context provider!");

		try {
			ContextProvider instance = (ContextProvider) ClassFactory.newInstanceOf(provider);
			register(clazz, instance);
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

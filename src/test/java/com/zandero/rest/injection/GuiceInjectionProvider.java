package com.zandero.rest.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 */
public class GuiceInjectionProvider extends AbstractModule implements InjectionProvider  {

	private Injector injector;

	public GuiceInjectionProvider() {

		injector = Guice.createInjector(this);
	}

	@Override
	protected void configure() {
		bind(DummyService.class).to(DummyServiceImpl.class);
		bind(OtherService.class).to(OtherServiceImpl.class);
	}

	@Override
	public Object inject(Class clazz) {

		return injector.getInstance(clazz);
	}
}

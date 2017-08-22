package com.zandero.rest;

import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to build up RestRouter with all writers, readers, handlers and context providers in one place
 */
public class RestBuilder {

	private final Vertx vertx;

	private List<Object> apis = new ArrayList<>();
	private List<Class<? extends ExceptionHandler>> exceptionHandlers = new ArrayList<>();
	private List<Class<? extends HttpResponseWriter>> responseWriters = new ArrayList<>();
	private List<Class<? extends ValueReader>> valueReaders = new ArrayList<>();
	private List<Class<? extends ContextProvider>> contextProviders = new ArrayList<>();

	public RestBuilder(Vertx vertx) { // hide

		this.vertx = vertx;
	}

	public RestBuilder register(Object... restApi) {

		apis.addAll(Arrays.asList(restApi));
		return this;
	}

	public RestBuilder errorHandler(Class<? extends ExceptionHandler>... handlers) {

		exceptionHandlers.addAll(Arrays.asList(handlers));
		return this;
	}

	public RestBuilder writer(Class<? extends HttpResponseWriter>... writers) {

		responseWriters.addAll(Arrays.asList(writers));
		return this;
	}

	public RestBuilder reader(Class<? extends ValueReader>... readers) {

		valueReaders.addAll(Arrays.asList(readers));
		return this;
	}

	public RestBuilder context(Class<? extends ContextProvider>... providers) {

		contextProviders.addAll(Arrays.asList(providers));
		return this;
	}

	public RestRouter build() {
		return null;
	}

	public RestRouter build(RestRouter restRouter) {
		return null;
	}
}

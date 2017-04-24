package com.zandero.rest.reader;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.ClassFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * Provides definition and caching of request body reader implementations
 */
public class ReaderFactory extends ClassFactory<HttpRequestBodyReader> {

	private final static Logger log = LoggerFactory.getLogger(ReaderFactory.class);

	public ReaderFactory() {

		super();
	}

	@Override
	protected void init() {

		if (classTypes.size() == 0) {
			classTypes.put(String.class.getName(), GenericBodyReader.class);

			mediaTypes.put(MediaType.APPLICATION_JSON, JsonBodyReader.class);
			mediaTypes.put(MediaType.TEXT_PLAIN, GenericBodyReader.class);
		}
	}

	/**
	 * Provides request body converter
	 *
	 * @param returnType method return type
	 * @param definition route definition
	 * @return reader to convert request body
	 */
	public HttpRequestBodyReader getRequestBodyReader(Class<?> returnType, RouteDefinition definition) {

		try {
			HttpRequestBodyReader reader = get(returnType, definition.getReader(), definition.getConsumes());
			return reader != null ? reader : new GenericBodyReader();
		}
		catch (ClassFactoryException e) {
			log.error("Failed to provide request body reader: " + returnType + ", for: " + definition + ", falling back to GenericBodyReader() instead!");
			return new GenericBodyReader();
		}
	}
}

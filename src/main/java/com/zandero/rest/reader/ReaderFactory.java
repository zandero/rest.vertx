package com.zandero.rest.reader;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * Provides definition and caching of request body reader implementations
 */
public class ReaderFactory extends ClassFactory<ValueReader> {

	private final static Logger log = LoggerFactory.getLogger(ReaderFactory.class);

	public ReaderFactory() {

		super();
	}

	@Override
	protected void init() {

		classTypes.put(String.class, GenericValueReader.class);

		mediaTypes.put(MediaType.APPLICATION_JSON, JsonValueReader.class);
		mediaTypes.put(MediaType.TEXT_PLAIN, GenericValueReader.class);
	}

	/**
	 * Provides request body converter
	 *
	 * @param definition route definition
	 * @return reader to convert request body
	 */
	/*public ValueReader getRequestBodyReader(RouteDefinition definition) {

		Class<?> readerType = null;

		try {
			// find body argument
			MethodParameter parameter = definition.getBodyParameter();
			Assert.notNull(parameter, "No body reader present for " + definition + ", register body reader via @RequestReader or RestRouter.getReaders().register()");

			readerType = parameter.getDataType();

			ValueReader reader = get(readerType, definition.getReader(), definition.getConsumes());
			return reader != null ? reader : new GenericBodyReader();
		} catch (ClassFactoryException e) {
			log.error("Failed to provide request body reader: " + readerType + ", for: " + definition + ", falling back to GenericBodyReader() instead!");
			return new GenericBodyReader();
		}
	}*/

	/**
	 * Step over all possibilities to provide desired reader
	 * @param parameter check parameter if reader is set or we have a type reader present
	 * @param method check default definition
	 * @param mediaType check by consumes annotation
	 * @return found reader or GenericBodyReader
	 */
	public ValueReader get(MethodParameter parameter, Class<? extends ValueReader> method, MediaType... mediaType) {

		// by type
		Class<?> readerType = null;
		try {

			// reader parameter
			Class<? extends ValueReader> reader = parameter.getReader();
			if (reader != null) {
				return getClassInstance(reader);
			}

			Assert.notNull(parameter, "Missing parameter!");
			readerType = parameter.getDataType();

			ValueReader valueReader = get(readerType, method, mediaType);
			return valueReader != null ? valueReader : new GenericValueReader();
		} catch (ClassFactoryException e) {

			log.error("Failed to provide value reader: " + readerType + ", for: " + parameter+ ", falling back to GenericBodyReader() instead!");
			return new GenericValueReader();
		}
	}

	public void register(Class<?> aClass, Class<? extends ValueReader> clazz) {

		Assert.notNull(aClass, "Missing request body class!");
		Assert.notNull(clazz, "Missing request reader type class");

		super.register(aClass, clazz);
	}

	public void register(String mediaType, Class<? extends ValueReader> clazz) {

		super.register(mediaType, clazz);
	}

	public void register(MediaType mediaType, Class<? extends ValueReader> clazz) {

		super.register(mediaType, clazz);
	}
}

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
	 * Step over all possibilities to provide desired reader
	 * @param parameter check parameter if reader is set or we have a type reader present
	 * @param byMethodDefinition check default definition
	 * @param mediaType check by consumes annotation
	 * @return found reader or GenericBodyReader
	 */
	public ValueReader get(MethodParameter parameter, Class<? extends ValueReader> byMethodDefinition, MediaType... mediaType) {

		// by type
		Class<?> readerType = null;
		try {

			// reader parameter as given
			Assert.notNull(parameter, "Missing parameter!");
			Class<? extends ValueReader> reader = parameter.getReader();
			if (reader != null) {
				return getClassInstance(reader);
			}

			// by value type, if body also by method/class definition or consumes media type  
			readerType = parameter.getDataType();

			ValueReader valueReader = get(readerType, byMethodDefinition, mediaType);
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

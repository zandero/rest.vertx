package com.zandero.rest.reader;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ContextException;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.Assert;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
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

		// pre fill with most generic implementation
		mediaTypes.put(MediaType.APPLICATION_JSON, JsonValueReader.class);
		mediaTypes.put(MediaType.TEXT_PLAIN, GenericValueReader.class);
	}

	/**
	 * Step over all possibilities to provide desired reader
	 *
	 * @param parameter          check parameter if reader is set or we have a type reader present
	 * @param byMethodDefinition check default definition
	 * @param provider           injection provider if any
	 * @param context            routing context
	 * @param mediaTypes         check by consumes annotation
	 * @return found reader or GenericBodyReader
	 */
	public ValueReader get(MethodParameter parameter,
	                       Class<? extends ValueReader> byMethodDefinition,
	                       InjectionProvider provider,
	                       RoutingContext context,
	                       MediaType... mediaTypes) {

		// by type
		Class<?> readerType = null;
		try {

			// reader parameter as given
			Assert.notNull(parameter, "Missing parameter!");
			Class<? extends ValueReader> reader = parameter.getReader();
			if (reader != null) {
				return getClassInstance(reader, provider, context);
			}

			// by value type, if body also by method/class definition or consumes media type  
			readerType = parameter.getDataType();

			ValueReader valueReader = get(readerType, byMethodDefinition, provider, context, mediaTypes);
			return valueReader != null ? valueReader : new GenericValueReader();
		}
		catch (ClassFactoryException e) {

			log.error("Failed to provide value reader: " + readerType + ", for: " + parameter + ", falling back to GenericBodyReader() instead!");
			return new GenericValueReader();
		}
		catch (ContextException e) {

			log.error(
				"Failed inject context into value reader: " + readerType + ", for: " + parameter + ", falling back to GenericBodyReader() instead!");
			return new GenericValueReader();
		}
	}

	/**
	 * Takes media type from @Consumes annotation if specified,
	 * @param reader
	 */
	public void register(Class<? extends ValueReader> reader) {

		Consumes found = reader.getAnnotation(Consumes.class);
		Assert.notNull(found, "Annotate reader with @Consumes to provide media type information!");

		MediaType[] consumes = MediaTypeHelper.getMediaTypes(found.value());
		if (consumes != null && consumes.length > 0) {
			for (MediaType type : consumes) {
				super.register(type, reader);
			}
		}
	}

	public void register(Class<?> clazz, Class<? extends ValueReader> reader) {

		Assert.notNull(clazz, "Missing read in class!");
		Assert.notNull(reader, "Missing request reader type class!");

		super.register(clazz, reader);
	}

	public void register(Class<?> clazz, ValueReader reader) {

		Assert.notNull(clazz, "Missing read in class!");
		Assert.notNull(reader, "Missing request reader type class!");

		super.register(clazz, reader);
	}

	public void register(String mediaType, Class<? extends ValueReader> clazz) {

		super.register(mediaType, clazz);
	}

	public void register(MediaType mediaType, Class<? extends ValueReader> clazz) {

		super.register(mediaType, clazz);
	}

	public void register(MediaType mediaType, ValueReader clazz) {

		super.register(mediaType, clazz);
	}
}

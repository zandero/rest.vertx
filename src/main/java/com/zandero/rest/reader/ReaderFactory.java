package com.zandero.rest.reader;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ReaderFactory {

	private final static Logger log = LoggerFactory.getLogger(ReaderFactory.class);


	// map of readers by class type
	private Map<String, Class<? extends HttpRequestBodyReader>> CLASS_TYPE_READERS = new HashMap<>();

	// map of readert by consumes / media type
	private Map<String, Class<? extends HttpRequestBodyReader>> MEDIA_TYPE_READERS = new HashMap<>();

	public ReaderFactory() {

		init();
	}

	private void init() {

		if (CLASS_TYPE_READERS.size() == 0) {
			CLASS_TYPE_READERS.put(String.class.getName(), GenericBodyReader.class);

			MEDIA_TYPE_READERS.put(MediaType.APPLICATION_JSON, JsonBodyReader.class);
			MEDIA_TYPE_READERS.put(MediaType.TEXT_PLAIN, GenericBodyReader.class);
		}
	}

	public void clear() {

		CLASS_TYPE_READERS.clear();
		MEDIA_TYPE_READERS.clear();

		init();
	}

	/**
	 * Provides request body converter
	 *
	 * @param returnType method return type
	 * @param definition route definition
	 * @return reader to convert request body
	 */
	public HttpRequestBodyReader getRequestBodyReader(Class<?> returnType, RouteDefinition definition) {

		Class<? extends HttpRequestBodyReader> reader = definition.getReader();

		// 2. if no writer is specified ... try to find appropriate writer by response type
		if (reader == null) {

			if (returnType != null) {
				// try to find appropriate writer if mapped
				reader = CLASS_TYPE_READERS.get(returnType.getName());
			}
		}

		if (reader == null) { // try by consumes

			MediaType[] consumes = definition.getConsumes();
			if (consumes != null && consumes.length > 0) {

				for (MediaType type : consumes) {
					reader = getRequestBodyReader(type);
					if (reader != null) {
						break;
					}
				}
			}
		}

		if (reader != null) {

			return getRequestBodyReaderInstance(reader);
		}

		// fall back to generic writer ...
		return new GenericBodyReader();
	}

	private Class<? extends HttpRequestBodyReader> getRequestBodyReader(MediaType mediaType) {

		if (mediaType == null) {
			return null;
		}

		return MEDIA_TYPE_READERS.get(MediaTypeHelper.getKey(mediaType));
	}

	private HttpRequestBodyReader getRequestBodyReaderInstance(Class<? extends HttpRequestBodyReader> reader) {

		if (reader != null) {
			try {
				// TODO .. might be a good idea to cache reader instances for some time
				return reader.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				log.error("Failed to instantiate request body reader '" + reader.getName() + "' " + e.getMessage(), e);
				// TODO: probably best to throw exception here
			}
		}

		return null;
	}
}

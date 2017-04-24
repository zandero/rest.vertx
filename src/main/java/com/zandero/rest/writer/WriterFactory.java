package com.zandero.rest.writer;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class WriterFactory {

	private final static Logger log = LoggerFactory.getLogger(WriterFactory.class);

	// map of writers by class type
	private Map<String, Class<? extends HttpResponseWriter>> CLASS_TYPE_WRITERS = new HashMap<>();

	// map of writers by response content type / media type
	private Map<String, Class<? extends HttpResponseWriter>> MEDIA_TYPE_WRITERS = new HashMap<>();

	public WriterFactory() {

		initWriters();
	}

	private void initWriters() {

		if (CLASS_TYPE_WRITERS.size() == 0) {

			CLASS_TYPE_WRITERS.put(Response.class.getName(), JaxResponseWriter.class);

			MEDIA_TYPE_WRITERS.put(MediaType.APPLICATION_JSON, JsonResponseWriter.class);
			MEDIA_TYPE_WRITERS.put(MediaType.TEXT_PLAIN, GenericResponseWriter.class);
		}
	}

	public void clear() {

		// clears any additionally registered writers and initializes defaults
		CLASS_TYPE_WRITERS.clear();
		MEDIA_TYPE_WRITERS.clear();

		initWriters();
	}

	public void registerWriter(String mediaType, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer!");

		MediaType type = MediaType.valueOf(mediaType);
		Assert.notNull(type, "Unknown media type given: " + mediaType);

		String key = MediaTypeHelper.getKey(type);
		MEDIA_TYPE_WRITERS.put(key, writer);
	}

	public void registerWriter(MediaType mediaType, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(mediaType, "Missing media type!");
		Assert.notNull(writer, "Missing response writer!");

		String key = MediaTypeHelper.getKey(mediaType);
		MEDIA_TYPE_WRITERS.put(key, writer);
	}

	public void registerWriter(Class<?> response, Class<? extends HttpResponseWriter> writer) {

		Assert.notNull(response, "Missing response class!");
		Assert.notNull(writer, "Missing response writer!");

		CLASS_TYPE_WRITERS.put(response.getName(), writer);
	}

	/**
	 * Finds assigned response writer or tries to assign a writer according to produces annotation and result type
	 *
	 * @param returnType type of result
	 * @param definition method definition
	 * @return writer to be used to produce response, {@see GenericResponseWriter} in case no suitable writer could be found
	 */
	public HttpResponseWriter getResponseWriter(Class<?> returnType, RouteDefinition definition) {

		// 1. if route has a explicit writer defined ... then return this writer
		Class<? extends HttpResponseWriter> writer = definition.getWriter();

		// 2. if no writer is specified ... try to find appropriate writer by response type
		if (writer == null) {

			if (returnType == null) {
				writer = NoContentResponseWriter.class;
			}
			else {
				// try to find appropriate writer if mapped
				writer = CLASS_TYPE_WRITERS.get(returnType.getName());
			}
		}

		if (writer == null) { // try by produces

			MediaType[] produces = definition.getProduces();
			if (produces != null && produces.length > 0) {

				for (MediaType type : produces) {
					writer = getResponseWriter(MediaTypeHelper.getKey(type));
					if (writer != null) {
						break;
					}
				}
			}
		}

		if (writer != null) {

			return getResponseWriterInstance(writer);
		}

		// fall back to generic writer ...
		return new GenericResponseWriter();
	}

	private Class<? extends HttpResponseWriter> getResponseWriter(String mediaType) {

		if (mediaType == null) {
			return null;
		}

		MediaType type = MediaType.valueOf(mediaType);
		return MEDIA_TYPE_WRITERS.get(MediaTypeHelper.getKey(type));
	}

	private static HttpResponseWriter getResponseWriterInstance(Class<? extends HttpResponseWriter> writer) {

		if (writer != null) {
			try {
				// TODO .. might be a good idea to cache writer instances for some time
				return writer.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				log.error("Failed to instantiate response writer '" + writer.getName() + "' " + e.getMessage(), e);
				// TODO: probably best to throw exception here
			}
		}

		return null;
	}

	public HttpResponseWriter getResponseWriterInstance(String mediaType) {

		Class<? extends HttpResponseWriter> writer = getResponseWriter(mediaType);
		return getResponseWriterInstance(writer);
	}
}

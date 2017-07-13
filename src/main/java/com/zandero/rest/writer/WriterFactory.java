package com.zandero.rest.writer;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.ClassFactoryException;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;

/**
 * Provides definition and caching of response writer implementations
 */
public class WriterFactory extends ClassFactory<HttpResponseWriter> {

	private final static Logger log = LoggerFactory.getLogger(WriterFactory.class);

	public WriterFactory() {

		super();
	}

	@Override
	protected void init() {

		// TODO: classTypes.put(Throwable.class, JaxResponseWriter.class);
		classTypes.put(Response.class, JaxResponseWriter.class);
		classTypes.put(HttpServerResponse.class, VertxResponseWriter.class);
		classTypes.put(Throwable.class, GenericExceptionWriter.class);

		mediaTypes.put(MediaType.APPLICATION_JSON, JsonResponseWriter.class);
		mediaTypes.put(MediaType.TEXT_PLAIN, GenericResponseWriter.class);
	}

	/**
	 * Finds assigned response writer or tries to assign a writer according to produces annotation and result type
	 *
	 * @param returnType type of result
	 * @param definition method definition
	 * @return writer to be used to produce response, or {@link GenericResponseWriter} in case no suitable writer could be found
	 */
	public HttpResponseWriter getResponseWriter(Class<?> returnType, RouteDefinition definition) {

		try {
			HttpResponseWriter writer = get(returnType, definition.getWriter(), definition.getProduces());
			return writer != null ? writer : new GenericResponseWriter();
		} catch (ClassFactoryException e) {
			log.error("Failed to provide response writer: " + returnType + ", for: " + definition + ", falling back to GenericResponseWriter() instead!");
			return new GenericResponseWriter();
		}
	}

	public HttpResponseWriter getResponseWriter(Class<? extends HttpResponseWriter> clazz) {

		try {
			HttpResponseWriter writer = getClassInstance(clazz);
			return writer != null ? writer : new GenericResponseWriter();
		} catch (ClassFactoryException e) {
			log.error("Failed to provide response writer: " + clazz + ", falling back to GenericResponseWriter() instead!");
			return new GenericResponseWriter();
		}
	}

	public HttpResponseWriter getFailureWriter(Class<? extends HttpResponseWriter>[] writers,
	                                           Class<? extends HttpResponseWriter> defaultWriter,
	                                           Class<? extends Throwable> aClass,
	                                           RouteDefinition definition) {

		// trickle down ... from definition to default handler
		Class<? extends HttpResponseWriter> found = null;

		if (writers == null || writers.length == 0) {
			found = defaultWriter;
		}
		else {

			for (Class<? extends HttpResponseWriter> writer: writers) {

				Type type = getGenericType(writer);
				if (checkIfCompatibleTypes(aClass, type)) {
					found = writer;
					break;
				}
			}
		}

		if (found == null) {
			return getResponseWriter(aClass, definition);
		}

		try {
			return super.getClassInstance(found);

		} catch (ClassFactoryException ex) {

			log.error(ex.getMessage());
			return getFailureWriter(null, GenericResponseWriter.class, aClass, definition);
		}
	}
}

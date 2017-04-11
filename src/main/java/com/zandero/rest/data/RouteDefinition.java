package com.zandero.rest.data;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.writer.GenericResponseWriter;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;

/**
 * Holds definition of a route as
 */
public class RouteDefinition {

	private final static Logger log = LoggerFactory.getLogger(RouteDefinition.class);

	private final String DELIMITER = "/";

	private String path = DELIMITER;

	private String[] consumes = null;

	private String[] produces = null;

	private io.vertx.core.http.HttpMethod method;

	private Class<HttpResponseWriter> writer;

	public RouteDefinition(Annotation[] annotations) {

		init(annotations);
	}

	public RouteDefinition(RouteDefinition base, Annotation[] annotations) {

		// copy base route
		path(base.getPath());

		consumes = base.getConsumes();
		produces = base.getProduces();
		method = base.getMethod();

		// complement / override with additional annotations
		init(annotations);
	}

	private void init(Annotation[] annotations) {

		for (Annotation annotation : annotations) {
			//log.info(annotation.toString());

			if (annotation instanceof Path) {
				path(((Path) annotation).value());
			}

			if (annotation instanceof Produces) {
				produces(((Produces) annotation).value());
			}

			if (annotation instanceof Consumes) {
				consumes(((Consumes) annotation).value());
			}

			if (annotation instanceof GET ||
				annotation instanceof POST ||
				annotation instanceof PUT ||
				annotation instanceof DELETE ||
				annotation instanceof HEAD ||
				annotation instanceof OPTIONS) {

				method(annotation.annotationType().getSimpleName());
			}

			// ToDo query params ...

			// response writer ...
			if (annotation instanceof ResponseWriter) {

				writer = ((ResponseWriter) annotation).value();
			}
		}
	}

	public RouteDefinition path(String subPath) {

		Assert.notNullOrEmptyTrimmed(subPath, "Missing or empty route '@Path'!");

		if (subPath.length() == 1 && DELIMITER.equals(subPath)) {
			return this;
		}

		if (!subPath.startsWith(DELIMITER)) {
			subPath = DELIMITER + subPath; // add leading "/"
		}

		if (subPath.endsWith(DELIMITER)) {
			subPath = subPath.substring(0, subPath.length() - 1); // remove trailing "/"
		}

		if (DELIMITER.equals(path)) { // default
			path = subPath;
		}
		else {
			path = path + subPath;
		}

		// ToDo ... get pathParams ...
		return this;
	}

	public RouteDefinition consumes(String[] value) {

		Assert.notNullOrEmpty(value, "Missing '@Consumes' definition!");
		consumes = value;
		return this;
	}

	public RouteDefinition produces(String[] value) {

		Assert.notNullOrEmpty(value, "Missing '@Produces' definition!");
		produces = value;
		return this;
	}

	private RouteDefinition method(String value) {

		for (HttpMethod item : HttpMethod.values()) {
			if (StringUtils.equals(value, item.name(), true)) {
				method = item;
				break;
			}
		}

		return this;
	}

	public String getPath() {

		return path;
	}

	public String[] getConsumes() {

		return consumes;
	}

	public String[] getProduces() {

		return produces;
	}

	public HttpMethod getMethod() {

		return method;
	}

	public Class<HttpResponseWriter> getWriter() {

		return writer;
	}

	@Override
	public String toString() {

		return method + " " + path;
	}
}

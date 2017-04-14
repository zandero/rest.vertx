package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;

import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Holds definition of a route as defined with annotations
 */
public class RouteDefinition {

	private final static Logger log = LoggerFactory.getLogger(RouteDefinition.class);

	private final String DELIMITER = "/";

	private String path = DELIMITER;

	private String[] consumes = null;

	private String[] produces = null;

	private io.vertx.core.http.HttpMethod method;

	private Class<? extends HttpResponseWriter> writer;

	private Map<String, MethodParameter> params;

	private String defaultValue;

	public RouteDefinition(Class clazz) {

		Class annotatedClass = AnnotationProcessor.getClassWithAnnotation(clazz, Path.class);
		if (annotatedClass == null) {
			annotatedClass = clazz;
		}

		init(annotatedClass.getAnnotations());
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

			if (annotation instanceof PathParam) {
				// find path param and determine argument index and to be extracted from request
			}

			if (annotation instanceof QueryParam) {
				// add query param ... to be extracted from request once method is called

			}


			if (annotation instanceof Produces) {
				produces(((Produces) annotation).value());
			}

			if (annotation instanceof Consumes) {
				consumes(((Consumes) annotation).value());
			}

			if (annotation instanceof javax.ws.rs.HttpMethod) {
				method(((javax.ws.rs.HttpMethod) annotation).value());
			}

			if (annotation instanceof GET ||
				annotation instanceof POST ||
				annotation instanceof PUT ||
				annotation instanceof DELETE ||
				annotation instanceof HEAD ||
				annotation instanceof OPTIONS) {

				method(annotation.annotationType().getSimpleName());
			}

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

		// convert path to Vert.X format
		subPath = PathConverter.convert(subPath);

		// extract parameters if any
		List<MethodParameter> params = PathConverter.extract(subPath);
		params(params);

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

	private RouteDefinition params(List<MethodParameter> pathParams) {

		if (pathParams == null || pathParams.size() == 0) {
			return this;
		}

		if (params == null) {
			params = new HashMap<>();
		}

		// check if param is already present
		for (MethodParameter parameter : pathParams) {
			if (params.get(parameter.getName()) != null) {
				// TODO throw exception
			}

			params.put(parameter.getName(), parameter);
		}

		return this;
	}

	public void setParameters(Class<?>[] parameterTypes, Annotation[][] parameters) {

		int index = 0;
		for (Annotation[] ann : parameters) {

			for (Annotation annotation: ann) {

				if (annotation instanceof PathParam) {
					// find path param ... and set index ...
					MethodParameter found = params.get(((PathParam) annotation).value());
					if (found == null) {
						// TODO throw exception
					}

					found.argument(parameterTypes[index], index);
				}

				if (annotation instanceof QueryParam) {
					// add param
					String name = ((QueryParam) annotation).value();
					if (params.get(name) != null) {
						// TODO throw exception
					}

					params.put(name, new MethodParameter(ParameterType.query, name, parameterTypes[index], index));
				}

				// TODO
				if (annotation instanceof FormParam) {

				}

				if (annotation instanceof HeaderParam) {

				}

				if (annotation instanceof Context) {

				}

				if (annotation instanceof DefaultValue) {
					defaultValue = ((DefaultValue) annotation).value();
				}
			}

			index ++;
		}
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

	public Class<? extends HttpResponseWriter> getWriter() {

		return writer;
	}

	public List<MethodParameter> getParameters() {

		if (params == null) {
			return Collections.emptyList();
		}

		ArrayList<MethodParameter> list = new ArrayList<>(params.values());
		list.sort(Comparator.comparing(MethodParameter::getIndex)); // sort parameters by index ...
		return list;
	}

	public String getDefaultValue() {

		return defaultValue;
	}

	@Override
	public String toString() {

		return method + " " + path;
	}
}

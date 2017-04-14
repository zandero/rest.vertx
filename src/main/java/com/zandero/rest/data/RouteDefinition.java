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

	private Map<String, MethodParameter> params = new HashMap<>();

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

			String name = null;
			ParameterType type = null;
			String defaultValue = null;

			for (Annotation annotation: ann) {

				if (annotation instanceof PathParam) {
					// find path param ... and set index ...
					name = ((PathParam) annotation).value();
					type = ParameterType.path;
				}

				if (annotation instanceof QueryParam) {
					// add param
					name = ((QueryParam) annotation).value();
					type = ParameterType.query;
				}

				if (annotation instanceof DefaultValue) {

					defaultValue = ((DefaultValue) annotation).value();
				}

				if (annotation instanceof FormParam) {

					type = ParameterType.form;
					name = ((FormParam) annotation).value();
				}

				if (annotation instanceof HeaderParam) {

					type = ParameterType.header;
					name = ((HeaderParam) annotation).value();
				}

				if (annotation instanceof Context) {

					type = ParameterType.context;
					name = parameterTypes[index].getName();
				}
			}

			// if no name provided than parameter is considered the request body
			if (name == null) {

				name = parameterTypes[index].getName();
				type = ParameterType.body;
			}

			MethodParameter parameter = provideParameter(name, type, defaultValue, parameterTypes[index], index);
			params.put(name, parameter);

			index ++;
		}
	}

	private MethodParameter provideParameter(String name, ParameterType type, String defaultValue, Class<?> parameterType, int index) {

		Assert.notNull(type, "Argument: " + name + " (" + parameterType + ") can't be provided with Vert.x request, check and annotate method arguments!");

		switch (type) {
			case path:
				MethodParameter found = params.get(name); // parameter should exist
				Assert.notNull(found, "Missing @PathParam: " + name + "(" + parameterType + ") as method argument!");

				found.argument(parameterType, index);
				found.setDefaultValue(defaultValue);
				return found;

			default:
				MethodParameter existing = params.get(name);
				Assert.isNull(existing, "Duplicate parameter: " + name + ", already provided!"); // param should not exist!

				MethodParameter newParam = new MethodParameter(type, name, parameterType, index);
				newParam.setDefaultValue(defaultValue);
				return newParam;
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

	public boolean requestHasBody() {

		return !(HttpMethod.GET.equals(method) ||
				 HttpMethod.HEAD.equals(method));
	}

	@Override
	public String toString() {

		return method + " " + path;
	}
}

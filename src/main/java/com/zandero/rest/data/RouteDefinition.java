package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.Blocking;
import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.reader.HttpRequestBodyReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Holds definition of a route as defined with annotations
 */
public class RouteDefinition {

	private final static Logger log = LoggerFactory.getLogger(RouteDefinition.class);

	private final String DELIMITER = "/";

	/**
	 * Original path
	 */
	private String path = DELIMITER;

	/**
	 * Converted path (the route), in case of regular expression paths
	 * otherwise null
	 */
	private String routePath = null;

	private MediaType[] consumes = null;

	private MediaType[] produces = null;

	private io.vertx.core.http.HttpMethod method;

	private Class<? extends HttpResponseWriter> writer;

	private Class<? extends HttpRequestBodyReader> reader;

	private Map<String, MethodParameter> params = new HashMap<>();

	private int order;

	private boolean blocking = false; // vert.x blocking

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

	/**
	 * Sets path secifics
	 *
	 * @param annotations list of method annotations
	 */
	private void init(Annotation[] annotations) {

		for (Annotation annotation : annotations) {
			//log.info(annotation.toString());

			if (annotation instanceof RouteOrder) {
				order(((RouteOrder) annotation).value());
			}

			if (annotation instanceof Path) {
				path(((Path) annotation).value());
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

			if (annotation instanceof RequestReader) {
				reader = ((RequestReader) annotation).value();
			}

			if (annotation instanceof Blocking) {
				blocking = ((Blocking) annotation).value();
			}
		}
	}

	private RouteDefinition order(int value) {

		order = value;
		return this;
	}

	public RouteDefinition path(String subPath) {

		Assert.notNullOrEmptyTrimmed(subPath, "Missing or empty route '@Path'!");

		// clean up path so all paths end with "/"
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

		// extract parameters if any
		List<MethodParameter> params = PathConverter.extract(path);
		params(params);

		// read path to Vert.X format
		routePath = PathConverter.convert(path);

		return this;
	}


	public RouteDefinition consumes(String[] value) {

		Assert.notNullOrEmpty(value, "Missing '@Consumes' definition!");
		consumes = getMediaTypes(value);
		return this;
	}

	public RouteDefinition produces(String[] value) {

		Assert.notNullOrEmpty(value, "Missing '@Produces' definition!");
		produces = getMediaTypes(value);
		return this;
	}

	private MediaType[] getMediaTypes(String[] value) {

		List<MediaType> types = new ArrayList<>();
		for (String item : value) {
			MediaType type = MediaType.valueOf(item);
			if (type != null) {
				types.add(type);
			}
		}

		if (types.size() == 0) {
			return null;
		}

		return types.toArray(new MediaType[]{});
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

		params.clear();

		// check if param is already present
		for (MethodParameter parameter : pathParams) {
			if (params.get(parameter.getName()) != null) {
				// TODO throw exception
			}

			params.put(parameter.getName(), parameter);
		}

		return this;
	}

	/**
	 * Extracts method arguments and links them with annotated route parameters
	 *
	 * @param method to extract argument types and annotations from
	 */
	public void setArguments(Method method) {

		Parameter[] parameters = method.getParameters();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();

		int index = 0;

		for (Annotation[] ann : annotations) {

			String name = null;
			ParameterType type = null;
			String defaultValue = null;

			for (Annotation annotation : ann) {

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
					name = parameters[index].getName();
					// todo check if context is supported and provide additional info, so context can be resolved
				}
			}

			// if no name provided than parameter is considered the request body
			if (name == null) {

				// try to find out what parameter type it is ... POST, PUT have a body ...
				// regEx path might not have a name ...
				MethodParameter param = findParameter(index);
				if (param != null) {

					Assert.isNull(param.getDataType(), "Duplicate argument type given: " + parameters[index].getName());
					param.argument(parameterTypes[index]); // set missing argument type
				}
				else {

					Assert.isTrue(requestHasBody(),
						"Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @Context) for: " + parameterTypes[index].getName() + " " + parameters[index].getName());

					name = parameters[index].getName();
					type = ParameterType.body;
				}
			}

			if (name != null) {
				MethodParameter parameter = provideArgument(name, type, defaultValue, parameterTypes[index], index);
				params.put(name, parameter);
			}

			index++;
		}
	}

	public MethodParameter findParameter(int index) {

		if (params == null) {
			return null;
		}

		for (MethodParameter parameter : params.values()) {
			if (parameter.getIndex() == index) {
				return parameter;
			}
		}

		return null;
	}

	private MethodParameter provideArgument(String name, ParameterType type, String defaultValue, Class<?> parameterType, int index) {

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
				Assert.isNull(existing, "Duplicate argument: " + name + ", already provided!"); // param should not exist!

				MethodParameter newParam = new MethodParameter(type, name, parameterType, index);
				newParam.setDefaultValue(defaultValue);
				return newParam;
		}
	}

	public String getPath() {

		return path;
	}

	public String getRoutePath() {

		if (pathIsRegEx()) { return regExPathEscape(routePath); }

		return routePath;
	}

	private String regExPathEscape(String path) {

		if (path == null) {
			return null;
		}

		return path.replaceAll("\\/", "\\\\/");
	}

	public MediaType[] getConsumes() {

		return consumes;
	}

	public MediaType[] getProduces() {

		return produces;
	}

	public HttpMethod getMethod() {

		return method;
	}

	public int getOrder() {

		return order;
	}

	public Class<? extends HttpResponseWriter> getWriter() {

		return writer;
	}

	public Class<? extends HttpRequestBodyReader> getReader() {

		return reader;
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

	public boolean pathIsRegEx() {

		if (params == null) {
			return false;
		}

		for (MethodParameter param : params.values()) {
			if (param.isRegEx()) {
				return true;
			}
		}

		return false;
	}

	public boolean isBlocking() {

		return blocking;
	}

	@Override
	public String toString() {

		return method + " " + routePath;
	}
}

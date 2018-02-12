package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.*;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.ArrayUtils;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

	private final String DELIMITER = "/";

	/**
	 * Original path as given in annotation
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

	private Class<? extends ValueReader> reader;

	private Class<? extends ExceptionHandler>[] exceptionHandlers;

	/**
	 * Parameters extracted from request path, query, headers, cookies ...
	 */
	private Map<String, MethodParameter> params = new HashMap<>();

	/**
	 * Route order lower is earlier or 0 for default
	 */
	private int order;

	/**
	 * Async response (don't close the writer)
	 */
	private boolean async;

	/**
	 * Type of return value ...
	 */
	private Class<?> returnType;

	/**
	 * Security
 	 */
	private Boolean permitAll = null; // true - permit all, false - deny all, null - check roles

	/**
	 * List of allowed roles or null if none
	 */
	private String[] roles = null;

	/**
	 * Suppress preventive failure checks - it might be that the check is to strong
	 */
	private boolean suppressCheck;

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

		reader = base.getReader();
		writer = base.getWriter();

		// set root privileges
		permitAll = base.getPermitAll();
		roles = base.roles;

		if (roles != null) {
			permitAll = null;
		}

		exceptionHandlers = base.getExceptionHandlers();

		// complement / override with additional annotations
		init(annotations);
	}

	public RouteDefinition(RoutingContext context) {

		Assert.notNull(context, "Missing context!");
		path(context.request().path());

		method = context.request().method();

		// take Accept and set as produces ...
		MediaType accept = MediaTypeHelper.valueOf(context.getAcceptableContentType());
		if (accept == null) {
			accept = MediaTypeHelper.valueOf(context.request().getHeader("Accept"));
		}

		if (accept != null) {
			produces = new MediaType[]{accept};
		}
	}

	/**
	 * Sets path specifics
	 *
	 * @param annotations list of method annotations
	 */
	private void init(Annotation[] annotations) {

		boolean hasPath = hasPath(annotations);

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

			if (annotation instanceof GET ||
			    annotation instanceof POST ||
			    annotation instanceof PUT ||
			    annotation instanceof DELETE ||
			    annotation instanceof HEAD ||
			    annotation instanceof OPTIONS ||
				annotation instanceof PATCH ||
				annotation instanceof TRACE ||
				annotation instanceof CONNECT) { // TODO: what about OTHER ?

				method(annotation.annotationType().getSimpleName());
			}

			// TODO: experiment to replace @Path with method value
			if (!hasPath && annotation instanceof TRACE) {
				path(((TRACE)annotation).value());
			}

			if (!hasPath && annotation instanceof CONNECT) {
				path(((CONNECT)annotation).value());
			}

			if (annotation instanceof javax.ws.rs.HttpMethod) {
				method(((javax.ws.rs.HttpMethod) annotation).value());
			}

			// response writer ...
			if (annotation instanceof ResponseWriter) {
				writer = ((ResponseWriter) annotation).value();
			}

			if (annotation instanceof RequestReader) {
				reader = ((RequestReader) annotation).value();
			}

			if (annotation instanceof RolesAllowed) {
				permitAll = null; // override any previous definition
				roles = ((RolesAllowed) annotation).value();
			}

			if (annotation instanceof DenyAll) {
				roles = null; // override any previous definition
				permitAll = false;
			}

			if (annotation instanceof PermitAll) {
				roles = null; // override any previous definition
				permitAll = true;
			}

			if (annotation instanceof CatchWith) {
				exceptionHandlers = ArrayUtils.join(((CatchWith) annotation).value(), exceptionHandlers);
			}

			if (annotation instanceof SuppressCheck) {
				suppressCheck = true;
			}
		}
	}

	private boolean hasPath(Annotation[] annotations) {
		for (Annotation annotation: annotations) {
			if (annotation instanceof Path) {
				return true;
			}
		}
		return false;
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
			subPath = subPath.substring(0, subPath.length() - 1); // loose trailing "/"
		}

		if (DELIMITER.equals(path)) { // default
			path = subPath;
		} else {
			path = path + subPath;
		}

		// extract parameters from path if any (
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
			MediaType type = MediaTypeHelper.valueOf(item);
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
				Assert.isNull(method, "Method already set to: " + method + "!");
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
				throw new IllegalArgumentException("Duplicate parameter name given: " + parameter.getName() + "! ");
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

		async = isAsync(method.getReturnType());
		returnType = method.getReturnType();

		int index = 0;
		Map<String, MethodParameter> arguments = new HashMap<>();

		for (Annotation[] ann : annotations) {

			String name = null;
			ParameterType type = null;
			String defaultValue = null;
			Class<? extends ValueReader> valueReader = null;

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

				if (annotation instanceof FormParam) {
					type = ParameterType.form;
					name = ((FormParam) annotation).value();
				}

				if (annotation instanceof CookieParam) {
					type = ParameterType.cookie;
					name = ((CookieParam) annotation).value();
				}

				if (annotation instanceof HeaderParam) {
					type = ParameterType.header;
					name = ((HeaderParam) annotation).value();
				}

				if (annotation instanceof MatrixParam) {
					type = ParameterType.matrix;
					name = ((MatrixParam) annotation).value();
				}

				if (annotation instanceof DefaultValue) {
					defaultValue = ((DefaultValue) annotation).value();
				}

				if (annotation instanceof RequestReader) {
					valueReader = ((RequestReader) annotation).value();
				}

				if (annotation instanceof Context) {
					type = ParameterType.context;
					name = parameters[index].getName();
				}
			}

			// if no name provided than parameter is considered the request body
			if (name == null) {

				// try to find out what parameter type it is ... POST, PUT have a body ...
				// regEx path might not have a name ...
				MethodParameter param = findParameter(index);
				if (param != null) {

					Assert.isNull(param.getDataType(), "Duplicate argument type given: " + parameters[index].getName());
					param.argument(parameterTypes[index], index); // set missing argument type and index
				} else {

					if (valueReader == null) {
						valueReader = reader; // take reader from method / class definition
					} else {
						reader = valueReader; // set body reader from field
					}

					Assert.isTrue(requestHasBody(),
					              "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam, @Context) for: " +
					              parameterTypes[index].getName() + " " + parameters[index].getName());

					name = parameters[index].getName();
					type = ParameterType.body;
				}
			}

			// collect only needed params for this method
			if (name != null) {
				MethodParameter parameter = provideArgument(name, type, defaultValue, parameterTypes[index], valueReader, index);
				arguments.put(name, parameter);
			}

			index++;
		}

		setUsedArguments(arguments);
	}

	/**
	 * @param returnType of REST method
	 * @return true if async operation, false otherwise (blocking operation)
	 */
	public static boolean isAsync(Class<?> returnType) {

		return (returnType != null && returnType.isAssignableFrom(Future.class));
	}

	private void setUsedArguments(Map<String, MethodParameter> arguments) {

		for (String key : params.keySet()) {
			if (arguments.containsKey(key)) {
				params.put(key, arguments.get(key)); // override existing
			}
		}

		for (String key : arguments.keySet()) {
			if (!params.containsKey(key)) {
				params.put(key, arguments.get(key)); // add missing
			}
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

		// try reg ex index
		if (pathIsRegEx()) {
			for (MethodParameter parameter : params.values()) {

				if (parameter.getRegExIndex() == index) {
					return parameter;
				}
			}
		}

		return null;
	}

	private MethodParameter provideArgument(String name,
	                                        ParameterType type,
	                                        String defaultValue,
	                                        Class<?> parameterType,
	                                        Class<? extends ValueReader> valueReader,
	                                        int index) {

		Assert.notNull(type,
		               "Argument: " + name + " (" + parameterType + ") can't be provided with Vert.x request, check and annotate method arguments!");

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
				newParam.setValueReader(valueReader);
				return newParam;
		}
	}

	public String getPath() {

		return path;
	}

	public String getRoutePath() {

		if (pathIsRegEx()) {
			return regExPathEscape(routePath);
		}

		return routePath;
	}

	private String regExPathEscape(String path) {

		if (path == null) {
			return null;
		}

		return path.replaceAll("/", "\\\\/");
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

	public Class<? extends ValueReader> getReader() {

		return reader;
	}

	public Class<? extends ExceptionHandler>[] getExceptionHandlers() {
		// join failure writers with global ... and return
		return exceptionHandlers;
	}

	public Class<?> getReturnType() {

		return returnType;
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

		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/
		// also see:
		// https://www.owasp.org/index.php/Test_HTTP_Methods_(OTG-CONFIG-006)
		return HttpMethod.POST.equals(method) ||
		       HttpMethod.PUT.equals(method) ||
			   HttpMethod.PATCH.equals(method) ||
			   HttpMethod.TRACE.equals(method);
	}

	public boolean hasBodyParameter() {

		return getBodyParameter() != null;
	}

	public MethodParameter getBodyParameter() {

		if (params == null) {
			return null;
		}

		return params.values().stream().filter(param -> ParameterType.body.equals(param.getType())).findFirst().orElse(null);
	}

	public boolean hasCookies() {

		if (params == null) {
			return false;
		}

		return params.values().stream().anyMatch(param -> ParameterType.cookie.equals(param.getType()));
	}

	public boolean hasMatrixParams() {

		if (params == null) {
			return false;
		}

		return params.values().stream().anyMatch(param -> ParameterType.matrix.equals(param.getType()));
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

	public boolean checkCompatibility() {
		return !suppressCheck;
	}

	/**
	 * @return true - permit all, false - deny all, null - check roles
	 */
	public Boolean getPermitAll() {

		return permitAll;
	}

	/**
	 * @return null - no roles defined, or array of allowed roles
	 */
	public String[] getRoles() {

		return roles;
	}

	/**
	 * @return true to check if User is in given role, false otherwise
	 */
	public boolean checkSecurity() {

		return permitAll != null || (roles != null && roles.length > 0);
	}

	public boolean isAsync() {
		return async;
	}

	@Override
	public String toString() {

		String prefix = "        "; // to improve formatting ...
		prefix = prefix.substring(0, prefix.length() - method.toString().length());

		String security = "";
		if (checkSecurity()) {

			if (permitAll != null) {
				security = permitAll ? " @PermitAll" : " @DenyAll";
			} else {
				security = "  [" + StringUtils.join(roles, ", ") + "]";
			}
		}

		return prefix + method + " " + routePath + security;
	}
}

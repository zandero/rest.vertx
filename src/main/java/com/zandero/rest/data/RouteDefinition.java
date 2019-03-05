package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.*;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.ArrayUtils;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Holds definition of a route as defined with annotations
 */
public class RouteDefinition {

	private final static Logger log = LoggerFactory.getLogger(RouteDefinition.class);

	private final String DELIMITER = "/";

	/**
	 * Path part given on class
	 */
	private String classPath = null;

	/**
	 * Path part given on method
	 */
	private String methodPath = null;

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

	/**
	 * Headers to add to response (additionally to content-type)
	 */
	private Map<String, String> headers = null;

	private io.vertx.core.http.HttpMethod method;

	private Class<? extends HttpResponseWriter> writer;

	private Class<? extends ValueReader> reader;

	private Class<? extends ContextProvider> contextProvider;

	private Class<? extends ExceptionHandler>[] exceptionHandlers;

	/**
	 * List of event(s) to be executed when REST is executed (using event bus)
	 */
	private List<Event> events;

	/**
	 * Parameters extracted from request path, query, headers, cookies ...
	 */
	private Map<String, MethodParameter> params = new LinkedHashMap<>();

	/**
	 * Route order lower is earlier or 0 for default
	 */
	private int order;

	/**
	 * Async response (don't close the writer)
	 */
	private boolean async;

	/**
	 * Execute blocking parallel or serial
	 */
	private boolean blockingOrdered;

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

		init(clazz.getAnnotations());
	}

	public RouteDefinition(RouteDefinition base, Method classMethod) {

		// copy base route
		path(base.getPath());

		consumes = base.getConsumes();
		produces = base.getProduces();
		method = base.getMethod();

		reader = base.getReader();
		writer = base.getWriter();
		contextProvider = base.getContextProvider();

		// set root privileges
		permitAll = base.getPermitAll();
		roles = base.roles;

		if (roles != null) {
			permitAll = null;
		}

		exceptionHandlers = ArrayUtils.join(exceptionHandlers, base.exceptionHandlers);

		// complement / override with additional annotations
		init(classMethod.getAnnotations());

		List<MethodParameter> pathParams = PathConverter.extract(path);
		params = join(params, pathParams);

		setArguments(classMethod);
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

	public RouteDefinition join(RouteDefinition additional) {

		if (additional == null) {
			return this;
		}

		if (method == null) {
			method = additional.method;
		}

		if (writer == null) {
			writer = additional.writer;
		}

		if (reader == null) {
			reader = additional.reader;
		}

		if (contextProvider == null) {
			contextProvider = additional.contextProvider;
		}

		if (!suppressCheck) {
			suppressCheck = additional.suppressCheck;
		}

		if (roles == null) {
			roles = additional.roles;
		}

		if (permitAll == null) {
			permitAll = additional.permitAll;
			roles = null;
		}

		if (!async) {
			async = additional.async;
		}

		if (!async && !blockingOrdered) {
			blockingOrdered = additional.blockingOrdered;
		}

		exceptionHandlers = ArrayUtils.join(exceptionHandlers, additional.exceptionHandlers);
		consumes = join(consumes, additional.consumes);
		produces = join(produces, additional.produces);

		// path change
		boolean pathChange = false;
		if (classPath == null && additional.classPath != null) {
			pathChange = true;
			classPath = additional.classPath;
		}

		if (methodPath == null && additional.methodPath != null) {
			pathChange = true;
			methodPath = additional.methodPath;
		}

		if (pathChange) { // reset and re-evaluate path
			path = DELIMITER;
			routePath = null;

			path(classPath);
			path(methodPath);

			// join data from path with already known additional params
			List<MethodParameter> pathParams = PathConverter.extract(path);
			additional.params = join(additional.params, pathParams);
		}

		// join collected params to base params
		params = join(params, additional.params);
		return this;
	}


	private static Map<String, MethodParameter> join(Map<String, MethodParameter> base, Map<String, MethodParameter> additional) {

		return join(base, additional.values());
	}

	private static Map<String, MethodParameter> join(Map<String, MethodParameter> base, Collection<MethodParameter> additional) {

		if (additional == null || additional.size() == 0) {
			return base;
		}

		Map<String, MethodParameter> out = new LinkedHashMap<>();

		Set<MethodParameter> found = new HashSet<>();
		for (String name : base.keySet()) {

			MethodParameter baseParam = base.get(name);

			boolean joined = false;
			for (MethodParameter additionalParam : additional) {

				if (baseParam.sameAs(additionalParam)) {
					baseParam.join(additionalParam);
					out.put(baseParam.getName(), baseParam);

					found.add(baseParam);
					joined = true;
				}
			}

			if (!joined) {
				out.put(name, baseParam);
			}
		}

		// add missing
		for (MethodParameter additionalParam : additional) {

			boolean newParam = true;
			for (MethodParameter present : found) {
				if (present.sameAs(additionalParam)) {
					newParam = false;
					break;
				}
			}

			if (newParam) {
				out.put(additionalParam.getName(), additionalParam);
				found.add(additionalParam);
			}
		}

		return out;
	}

	private static MediaType[] join(MediaType[] base, MediaType[] additional) {

		if (additional == null || additional.length == 0) {
			return base;
		}

		if (base == null) {
			return additional;
		}

		Set<MediaType> baseSet = new LinkedHashSet<>(Arrays.asList(base));
		Set<MediaType> addSet = new LinkedHashSet<>(Arrays.asList(additional));
		baseSet.addAll(addSet);

		return baseSet.toArray(new MediaType[]{});
	}

	/**
	 * Sets path specifics
	 *
	 * @param annotations list of method annotations
	 */
	private void init(Annotation[] annotations) {

		for (Annotation annotation : annotations) {

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

			if (annotation instanceof Header) {
				headers = headers(((Header) annotation).value());
			}

			if (annotation instanceof GET ||
			    annotation instanceof POST ||
			    annotation instanceof PUT ||
			    annotation instanceof DELETE ||
			    annotation instanceof HEAD ||
			    annotation instanceof OPTIONS ||
			    annotation instanceof PATCH) {
				method(annotation.annotationType().getSimpleName());
			}

			// Custom rest.vertx method, path, consumes and produces combination
			if (annotation instanceof Get ||
			    annotation instanceof Post ||
			    annotation instanceof Put ||
			    annotation instanceof Delete ||
			    annotation instanceof Head ||
			    annotation instanceof Options ||
			    annotation instanceof Patch ||
			    annotation instanceof Trace ||
			    annotation instanceof Connect) {

				method(annotation.annotationType().getSimpleName());

				// take path, consumes and produces values if given
				// we need to use proxy to get to the underlying methods
				InvocationHandler handler = Proxy.getInvocationHandler(annotation);
				try {
					path((String) handler.invoke(annotation, annotation.getClass().getMethod("value"), null));

					consumes((String[]) handler.invoke(annotation, annotation.getClass().getMethod("consumes"), null));
					produces((String[]) handler.invoke(annotation, annotation.getClass().getMethod("produces"), null));
				}
				catch (Throwable ex) {
					// should not happen ...
					log.warn("Failed to get annotation value via proxy!", ex);
				}
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

			if (annotation instanceof ContextReader) {
				contextProvider = ((ContextReader)annotation).value();
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
				exceptionHandlers =
					ArrayUtils.join(((CatchWith) annotation).value(), exceptionHandlers); // method handler is applied before class handler
			}

			if (annotation instanceof SuppressCheck) {
				suppressCheck = true;
			}

			if (annotation instanceof Blocking) {
				blockingOrdered = ((Blocking) annotation).value();
			}

			if (annotation instanceof Event) {
				Event event = (Event) annotation;
				addEvent(event);
			}

			if (annotation instanceof Events) {
				Events eventArray = (Events) annotation;
				if (eventArray.value().length > 0) {
					for (Event event : eventArray.value()) {
						addEvent(event);
					}
				}
			}
		}
	}

	private Map<String, String> headers(String[] value) {
		return AnnotationProcessor.getNameValuePairs(value);
	}

	private boolean hasPath(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Path) {
				return true;
			}
		}
		return false;
	}

	private RouteDefinition order(int value) {

		Assert.isTrue(value >= 0, "@RouteOrder must be >= 0!");

		order = value;
		return this;
	}

	public RouteDefinition path(String subPath) {

		Assert.notNullOrEmptyTrimmed(subPath, "Missing or empty route '@Path'!");

		subPath = PathConverter.clean(subPath);

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
			classPath = subPath;
		} else {
			path = path + subPath;
			methodPath = subPath;
		}

		// convert path to Vert.X format
		routePath = PathConverter.convert(path);
		return this;
	}


	public RouteDefinition consumes(String[] value) {

		Assert.notNullOrEmpty(value, "Missing '@Consumes' definition!");

		MediaType[] values = MediaTypeHelper.getMediaTypes(value);
		if (!MediaTypeHelper.isDefaultMediaType(values)) {
			consumes = values;
		}
		return this;
	}

	public RouteDefinition produces(String[] value) {

		Assert.notNullOrEmpty(value, "Missing '@Produces' definition!");
		MediaType[] values = MediaTypeHelper.getMediaTypes(value);
		if (!MediaTypeHelper.isDefaultMediaType(values)) {
			produces = MediaTypeHelper.getMediaTypes(value);
		}
		return this;
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

	private RouteDefinition addEvent(Event event) {
		if (events == null) {
			events = new ArrayList<>();
		}
		events.add(event);
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
			boolean raw = false;
			Class<? extends ValueReader> valueReader = null;
			Class<? extends ContextProvider> contextValueProvider = null;

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

				if (annotation instanceof Raw) {
					raw = true;
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

				if (annotation instanceof ContextReader) {
					contextValueProvider = ((ContextReader) annotation).value();
				}

				if (annotation instanceof Context) {
					type = ParameterType.context;
					name = parameters[index].getName();
				}
			}

			// if no name provided than parameter is considered unknown (it might be request body, but we don't know)
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

					name = parameters[index].getName();
					type = ParameterType.unknown;
				}
			}

			// collect only needed params for this method
			if (name != null) {

				// set context provider from method annotation if fitting
				if (contextValueProvider == null && contextProvider != null) {
					Type generic = ClassFactory.getGenericType(contextProvider);
				    if (ClassFactory.checkIfCompatibleTypes(parameterTypes[index], generic)) {
					    contextValueProvider = contextProvider;
				    }
				}

				MethodParameter parameter = provideArgument(name, type, defaultValue, raw, parameterTypes[index], valueReader, contextValueProvider, index);
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
	static boolean isAsync(Class<?> returnType) {

		return ClassFactory.checkIfCompatibleTypes(returnType, Future.class);
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

	MethodParameter findParameter(int index) {

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
	                                        boolean raw,
	                                        Class<?> parameterType,
	                                        Class<? extends ValueReader> valueReader,
	                                        Class<? extends ContextProvider> contextProvider,
	                                        int index) {


		Assert.notNull(type,
		               "Argument: " + name + " (" + parameterType + ") can't be provided with Vert.x request, check and annotate method arguments!");

		if (ParameterType.path.equals(type)) {
			MethodParameter found = params.get(name); // parameter should exist
			//Assert.notNull(found, "Missing @PathParam: " + name + "(" + parameterType + ") as method argument!");

			if (found != null) {
				found.argument(parameterType, index);
				found.setDefaultValue(defaultValue);
				return found;
			}
		}

		MethodParameter existing = params.get(name);
		Assert.isNull(existing, "Duplicate argument: " + name + ", already provided!"); // param should not exist!

		MethodParameter newParam = new MethodParameter(type, name, parameterType, index);
		newParam.setDefaultValue(defaultValue);
		newParam.setValueReader(valueReader);
		newParam.setContextProvider(contextProvider);
		if (raw) {
			newParam.setRaw();
		}
		return newParam;
	}

	public String getPath() {

		return path;
	}

	public String getRoutePath() {

		/*if (pathIsRegEx()) {
			return regExPathEscape(routePath);
		}*/

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

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Class<? extends ValueReader> getReader() {
		return reader;
	}

	public Class<? extends ContextProvider> getContextProvider() {
		return contextProvider;
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
		return HttpMethod.DELETE.equals(method) ||
			   HttpMethod.POST.equals(method) ||
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

	/**
	 * defines if execute blocking or async
	 *
	 * @return true async, false blocking
	 */
	public boolean isAsync() {
		return async;
	}

	/**
	 * Applies to executeBlocking only
	 *
	 * @return true to execute serially, false to execute parallel on worker pool (default false)
	 */
	public boolean executeBlockingOrdered() {
		return blockingOrdered;
	}

	/**
	 * @return List of mapped events to be executed or null if none provided
	 */
	public List<Event> getEvents() {
		return events;
	}

	@Override
	public String toString() {

		String prefix = "        "; // to improve formatting ...
		prefix = prefix.substring(0, prefix.length() - (method == null ? 0 : method.toString().length()));

		String security = "";
		if (checkSecurity()) {

			if (permitAll != null) {
				security = permitAll ? " @PermitAll" : " @DenyAll";
			} else {
				security = "  [" + StringUtils.join(roles, ", ") + "]";
			}
		}

		return prefix + (method == null ? ">undefined<" : method) + " " + routePath + security + (pathIsRegEx() ? "[regex]" : "");
	}
}

package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.*;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.exception.ExceptionHandler;
import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.*;
import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.annotation.security.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static com.zandero.rest.data.ClassUtils.*;

/**
 * Holds definition of a route as defined with annotations
 */
public class RouteDefinition {

    private final static Logger log = LoggerFactory.getLogger(RouteDefinition.class);
    private static final String CONTINUATION_CLASS = "kotlin.coroutines.Continuation";
    private static final String CONTINUATION_EXPERIMENTAL_CLASS = "kotlin.coroutines.experimental.Continuation";

    private static final Set<ParameterType> BODY_HANDLER_PARAMS = ArrayUtils.toSet(ParameterType.body,
                                                                                   ParameterType.bean,
                                                                                   ParameterType.form);

    private static final Set<HttpMethod> BODY_METHODS = ArrayUtils.toSet(HttpMethod.GET,
                                                                         HttpMethod.DELETE,
                                                                         HttpMethod.POST,
                                                                         HttpMethod.PUT,
                                                                         HttpMethod.PATCH,
                                                                         HttpMethod.TRACE);

    private final String DELIMITER = "/";

    /**
     * Path part given on class
     */
    protected String classPath = null;

    /**
     * Path part given on method
     */
    protected String methodPath = null;

    /**
     *
     */
    protected String applicationPath = null;

    /**
     * Original path as given in annotation
     */
    protected String path = DELIMITER;

    /**
     * Converted path (the route), in case of regular expression paths
     * otherwise null
     */
    protected String routePath = null;

    protected MediaType[] consumes = null;

    protected MediaType[] produces = null;

    /**
     * Headers to add to response (additionally to content-type)
     */
    protected Map<String, String> headers = null;

    protected io.vertx.core.http.HttpMethod method;

    protected Class<? extends HttpResponseWriter> writer;

    protected Class<? extends ValueReader> reader;

    protected Class<? extends ContextProvider> contextProvider;

    protected Class<? extends ExceptionHandler>[] exceptionHandlers;

    /**
     * List of event(s) to be executed when REST is executed (using event bus)
     */
    protected List<Event> events;

    /**
     * Parameters extracted from request path, query, headers, cookies ...
     */
    protected Map<String, MethodParameter> params = new LinkedHashMap<>();

    /**
     * Route order lower is earlier or 0 for default
     */
    protected int order;

    /**
     * Async response (don't close the writer)
     */
    protected boolean async;

    /**
     * Whether it is a kotlin suspendable operation.
     */
    protected boolean isSuspendable = false;

    /**
     * Execute blocking parallel or serial
     */
    protected boolean blockingOrdered;

    /**
     * Type of return value ...
     */
    protected Class<?> returnType;

    /**
     * Security
     */
    protected Boolean permitAll = null; // true - permit all, false - deny all, null - check roles

    /**
     * List of allowed roles or null if none
     */
    protected String[] roles = null;

    /**
     * Suppress preventive failure checks - it might be that the check is to strong
     */
    protected boolean suppressCheck;

    public RouteDefinition(Class clazz) {

        init(clazz.getAnnotations());
        evaluatePath();
    }

    public RouteDefinition(RouteDefinition base, Method classMethod) {

        // copy base route
        if (base.getApplicationPath() != null) {
            applicationPath(base.getApplicationPath());
        }

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
        evaluatePath();
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

        evaluatePath();
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

        if (roles == null && permitAll == null) {
            roles = additional.roles;
        }

        if (permitAll == null && roles == null) {
            permitAll = additional.permitAll;
            roles = null;
        }

        if (!async) {
            async = additional.async;
        }

        if (!isSuspendable) {
            isSuspendable = additional.isSuspendable;
        }

        if (!async && !isSuspendable && !blockingOrdered) {
            blockingOrdered = additional.blockingOrdered;
        }

        exceptionHandlers = ArrayUtils.join(exceptionHandlers, additional.exceptionHandlers);
        consumes = join(consumes, additional.consumes);
        produces = join(produces, additional.produces);

        // path change
        boolean pathChange = false;

        if (applicationPath == null && additional.applicationPath != null) {
            applicationPath = additional.applicationPath;
        }

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

            path(classPath);
            path(methodPath);

            // join data from path with already known additional params
            List<MethodParameter> pathParams = PathConverter.extract(path);
            additional.params = join(additional.params, pathParams);
        }

        // join collected params to base params
        params = join(params, additional.params);
        return evaluatePath();
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

        return MediaTypeHelper.join(base, additional);
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

            if (annotation instanceof ApplicationPath) {
                applicationPath(((ApplicationPath) annotation).value());
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
                } catch (Throwable ex) {
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
                contextProvider = ((ContextReader) annotation).value();
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

    private RouteDefinition applicationPath(String appPath) {
        Assert.notNullOrEmptyTrimmed(appPath, "Missing or empty application path '@ApplicationPath'!");
        applicationPath = PathConverter.clean(DELIMITER + appPath);
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

        return this;
    }

    private RouteDefinition evaluatePath() {
        String pathToConvert = path;
        if (applicationPath != null && !applicationPath.equals(DELIMITER)) {
            pathToConvert = applicationPath + path;
        }

        // convert path to Vert.X format
        routePath = PathConverter.convert(pathToConvert);
        return this;
    }

    public RouteDefinition consumes(String[] value) {

        Assert.notNullOrEmpty(value, "Missing '@Consumes' definition!");

        MediaType[] values = MediaTypeHelper.getMediaTypes(value);
        if (MediaTypeHelper.notDefaultMediaType(values)) {
            consumes = values;
        }
        return this;
    }

    public RouteDefinition produces(String[] value) {

        Assert.notNullOrEmpty(value, "Missing '@Produces' definition!");
        MediaType[] values = MediaTypeHelper.getMediaTypes(value);
        if (MediaTypeHelper.notDefaultMediaType(values)) {
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
        isSuspendable = isSuspendable(method);
        returnType = method.getReturnType();

        int index = 0;
        Map<String, MethodParameter> arguments = new HashMap<>();

        for (Annotation[] ann : annotations) {

            if (isSuspendable && index == parameters.length - 1) { // If it is a kotlin suspend method, last parameter's type is Continuation which must be ignored.
                break;
            }

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

                if (annotation instanceof BodyParam) {
                    type = ParameterType.body;
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

                if (annotation instanceof BeanParam) {
                    type = ParameterType.bean;
                    name = parameters[index].getName();
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
                    name = parameters[index].getName();
                    type = ParameterType.unknown;

                    // Body reader only .... (as we don't know param name we assume it is the body)
                    if (valueReader == null) {
                        valueReader = reader; // take reader from method / class definition
                    } else {
                        reader = valueReader; // set body reader from field
                    }
                }
            }

            // collect only needed params for this method
            if (name != null) {

                // set context provider from method annotation if fitting
                if (contextValueProvider == null && contextProvider != null) {
                    Type generic = getGenericType(contextProvider);
                    if (checkIfCompatibleType(parameterTypes[index], generic)) {
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

        return checkIfCompatibleTypes(returnType, Future.class, Promise.class);
    }

    /**
     * @param method REST method
     * @return true if is a kotlin suspendable operation, false otherwise
     */
    static boolean isSuspendable(Method method) {

        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 0) {
            return false;
        }

        String lastParameterType = parameterTypes[parameterTypes.length - 1].getName();

        return lastParameterType.equals(CONTINUATION_CLASS)
                   || lastParameterType.equals(CONTINUATION_EXPERIMENTAL_CLASS);
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

    public String getApplicationPath() {
        return applicationPath;
    }

    public String getPath() {
        return path;
    }

    public String getRoutePath() {
        return routePath;
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

    public boolean requestCanHaveBody() {
        return BODY_METHODS.contains(method);
    }

    public boolean requestHasBody() {
        return requestCanHaveBody() &&
                   params.values().stream().filter(param -> BODY_HANDLER_PARAMS.contains(param.getType()))
                       .findFirst().orElse(null) != null;
    }

    public boolean hasBodyParameter() {
        return getBodyParameter() != null;
    }

    public MethodParameter getBodyParameter() {

        if (params == null) {
            return null;
        }

        return params.values().stream().filter(param -> ParameterType.body.equals(param.getType()))
                   .findFirst().orElse(null);
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
     * Whether the REST method is a kotlin suspendable method?
     *
     * @return true if it is a kotlin suspendable method.
     */
    public boolean isSuspendable() {
        return isSuspendable;
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

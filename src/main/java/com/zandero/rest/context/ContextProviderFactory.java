package com.zandero.rest.context;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.ContextException;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.Assert;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Storage of context providers
 */
public class ContextProviderFactory extends ClassFactory<ContextProvider> {

	/**
	 * Cache of classes that need or don't need context injection
	 * If class needs context injection .. a list of Fields to inject is provided
	 * If class doesn't need context injection the list of fields in empty (not null)
	 */
	private static HashMap<String, List<Field>> contextCache = new HashMap<>();

	@Override
	protected void init() {
		// nothing to
	}

	public ContextProvider getContextProvider(InjectionProvider provider,
	                                          Class clazzType,
	                                          Class<? extends ContextProvider> aClass,
	                                          RoutingContext context) throws ClassFactoryException,
	                                                                         ContextException {


		return get(clazzType, aClass, provider, context, null);
	}

	public void register(Class<?> aClass, Class<? extends ContextProvider> clazz) {
		super.register(aClass, clazz);
	}

	public void register(Class<?> aClass, ContextProvider instance) {
		super.register(aClass, instance);
	}

	private static List<Field> checkForContext(Class<?> clazz) {

		// check if any class members are injected
		Field[] fields = clazz.getDeclaredFields();
		List<Field> contextFields = new ArrayList<>();
		for (Field field : fields) {
			Annotation found = field.getAnnotation(Context.class);
			if (found != null) {
				contextFields.add(field);
			}
		}

		return contextFields;
	}

	private static List<Field> getContextFields(Class<?> clazz) {

		List<Field> contextFields = contextCache.get(clazz.getName());
		if (contextFields == null) {
			contextFields = checkForContext(clazz);
			contextCache.put(clazz.getName(), contextFields);
		}

		return contextFields;
	}

	public static <T> boolean hasContext(Class<? extends T> clazz) {

		return getContextFields(clazz).size() > 0;
	}

	/**
	 * Provides vertx context of desired type if possible
	 *
	 * @param type         context type
	 * @param defaultValue default value if given
	 * @param context      to provider / extract values from
	 * @return found context or null if not found
	 * @throws ContextException in case context could not be provided
	 */
	public static Object provideContext(Class<?> type,
	                                    String defaultValue,
	                                    RoutingContext context) throws ContextException {

		// vert.x context
		if (type.isAssignableFrom(HttpServerResponse.class)) {
			return context.response();
		}

		if (type.isAssignableFrom(HttpServerRequest.class)) {
			return context.request();
		}

		if (type.isAssignableFrom(RoutingContext.class)) {
			return context;
		}

		// provide vertx via @Context
		if (type.isAssignableFrom(Vertx.class)) {
			return context.vertx();
		}

		// provide event bus via @Context
		if (type.isAssignableFrom(EventBus.class)) {
			return context.vertx().eventBus();
		}

		if (type.isAssignableFrom(User.class)) {
			return context.user();
		}

		// internal context
		if (type.isAssignableFrom(RouteDefinition.class)) {
			return new RouteDefinition(context);
		}

		// browse through context storage
		if (context.data() != null && context.data().size() > 0) {

			Object item = context.data().get(getContextKey(type));
			if (item != null) { // found in storage ... return
				return item;
			}
		}

		if (defaultValue != null) {
			// check if type has constructor that can be used with defaultValue ...
			// and create Context type on the fly constructed with defaultValue
			try {
				return ClassFactory.constructType(type, defaultValue);
			}
			catch (ClassFactoryException e) {
				throw new ContextException("Can't provide @Context of type: " + type + ". " + e.getMessage());
			}
		}

		throw new ContextException("Can't provide @Context of type: " + type);
	}

	public static void injectContext(Object instance, RoutingContext routeContext) throws ContextException {

		if (instance == null) {
			return;
		}

		List<Field> contextFields = getContextFields(instance.getClass());

		for (Field field : contextFields) {
			Annotation found = field.getAnnotation(Context.class);
			if (found != null) {

				Object context = provideContext(field.getType(), null, routeContext);
				try {
					field.setAccessible(true);
					field.set(instance, context);
				}
				catch (IllegalAccessException e) {
					throw new ContextException("Can't provide @Context for: " + field.getType() + " - " + e.getMessage());
				}
			}
		}
	}

	public static String getContextKey(Object object) {

		Assert.notNull(object, "Expected object but got null!");
		if (object instanceof Class) {
			return "RestRouter-" + ((Class) object).getName();
		}

		return "RestRouter-" + object.getClass().getName();
	}
}

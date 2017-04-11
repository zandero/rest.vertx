package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

/**
 * Builds up a vert.x route based on JAX-RS annotation provided in given class
 */
public class RestRouter {

	private final static Logger log = LoggerFactory.getLogger(RestRouter.class);

	/**
	 * Searches for annotations to register routes ...
	 *
	 * @param restApi to search
	 */
	public static <T>  Router register(Vertx vertx, Object restApi) {

		Assert.notNull(vertx, "Missing vertx!");
		Assert.notNull(restApi, "Missing REST API class object!");

		Router router = Router.router(vertx);

		Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(restApi.getClass());
		Iterator<RouteDefinition> iterator = definitions.keySet().iterator();

		while (iterator.hasNext()) {

			RouteDefinition definition = iterator.next();
			Method method = definitions.get(definition);

			Route route = router.route(definition.getMethod(), definition.getPath());
			log.info("Registering route: " + definition);

			if (definition.getConsumes() != null) {
				for (String item : definition.getConsumes()) {
					route.consumes(item);
				}
			}

			if (definition.getProduces() != null) {
				for (String item : definition.getProduces()) {
					route.produces(item);
				}
			}

			// bind method execution
			route.handler(getHandler(restApi, method));
		}

		return router;
	}

	private static Handler<RoutingContext> getHandler(final Object toInvoke, final Method method) {

		return context -> {

			try {
				Object result = method.invoke(toInvoke);

				// dummy response ... as proof of concept
				// TODO: add response builder according to definition produces
				context.response().setStatusCode(200).end(result.toString());
			}
			catch (IllegalAccessException | InvocationTargetException e) {
				// return 500 error with stack trace
				// e.printStackTrace();
				context.response().setStatusCode(500).end(e.getMessage());
			}
		};
	}
}

package com.zandero.rest.writer;

import com.zandero.utils.Assert;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class JaxResponseWriter implements HttpResponseWriter {

	@Override
	public void write(Object result, HttpServerResponse response) {

		Assert.notNull(result, "Expected result but got null!");
		Assert.isTrue(result instanceof Response, "Expected instance of: " + Response.class.getName() + ", but got: " + result.getClass().getName());

		if (result instanceof Response) {
			Response jax = (Response) result;

			response.setStatusCode(jax.getStatus());
			addHeaders(jax, response);

			if (jax.getEntity() != null) {

				response.end(jax.getEntity().toString()); // TODO ... use correct transformation of entity to string
			}
			else {
				response.end();
			}
		}
	}

	public static void addHeaders(Response jaxrsResponse, HttpServerResponse response) {

		if (jaxrsResponse.getMetadata() != null) {

			List<Object> cookies = jaxrsResponse.getMetadata().get(HttpHeaders.SET_COOKIE.toString());
			if (cookies != null) {

				Iterator<Object> it = cookies.iterator();
				while (it.hasNext()) {
					Object next = it.next();
					if (next instanceof NewCookie) {

						NewCookie cookie = (NewCookie) next;
						response.putHeader(HttpHeaders.SET_COOKIE, cookie.toString());

						it.remove();
					}
				}

				if (cookies.size() < 1) {
					jaxrsResponse.getMetadata().remove(HttpHeaders.SET_COOKIE.toString());
				}
			}
		}

		if (jaxrsResponse.getMetadata() != null && jaxrsResponse.getMetadata().size() > 0) {

			for (String name : jaxrsResponse.getMetadata().keySet()) {
				List<Object> meta = jaxrsResponse.getMetadata().get(name);

				if (meta != null && meta.size() > 0) {
					for (Object item : meta) {
						if (item != null) {
							response.putHeader(name, item.toString());
						}
					}
				}
			}
		}
	}
}

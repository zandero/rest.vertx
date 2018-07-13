package com.zandero.rest.test;

import com.zandero.rest.annotation.Event;
import com.zandero.rest.annotation.Events;
import com.zandero.rest.test.events.FailureEvent;
import com.zandero.rest.test.events.SimpleEvent;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("events")
public class TestEventsRest {

	@GET
	@Path("ok")
	@Event(SimpleEvent.class) // always triggered
	public Dummy returnBody() {
		return new Dummy("one", "event");
	}

	@GET
	@Path("error/{status}")
	@Events({@Event(SimpleEvent.class),
	         @Event(value = FailureEvent.class, exception = IllegalArgumentException.class), // triggered via exception thrown
	         @Event(value = SimpleEvent.class, response = 301)}) // triggered on response code 301
	public Dummy returnOrFail(@PathParam("status") int status, @Context HttpServerResponse response) {

		if (status >= 200 && status < 300) {
			return new Dummy("one", "event");
		}

		if (status >= 300 && status < 400) {
			response.setStatusCode(301);
			return new Dummy("two", "failed");
		}

		throw new IllegalArgumentException("Failed: " + status);
	}
}

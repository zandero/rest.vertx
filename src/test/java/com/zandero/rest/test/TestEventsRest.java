package com.zandero.rest.test;

import com.zandero.rest.annotation.Event;
import com.zandero.rest.test.events.SimpleEvent;
import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}

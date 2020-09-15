package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestCustomWriter;
import io.vertx.core.http.HttpServerRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Simple REST to test annotation processing
 */
@Produces("application/json")
@Path("/test")
public class TestRest {

    @GET
    @Path("/echo")
    @Produces(MediaType.TEXT_HTML)
    public String echo() {

        System.out.println("HELLO");
        return "Hello world!";
    }

    @GET
    @Path("/custom")
    @ResponseWriter(TestCustomWriter.class) // use custom writer for output
    public String custom() {

        return "CUSTOM";
    }

    @GET
    @Path("/jax")
    public Response jax() {

        return Response
                   .accepted("Hello")
                   .header("X-Test", "Test")
                   .build();
    }

    @GET
    @Path("/match/{this}/{that}")
    public Response match(@PathParam("this") String thisParam, @PathParam("that") String thatParam) {

        return Response.ok(thisParam + "/" + thatParam).build();
    }

    @GET
    @Path("/match2/:this/{that}")
    public Response matchTwo(@PathParam("this") String thisParam, @PathParam("that") String thatParam) {

        return Response.ok(thisParam + "/" + thatParam).build();
    }

    @GET
    @Path("/mix/:integer/{boolean}")
    public Response mixParams(@PathParam("integer") int intParam, @PathParam("boolean") boolean boolParam) {

        return Response.ok(intParam + "/" + boolParam).build();
    }

    @GET
    @Path("/mix2/:Integer/{char}")
    public Response mixParams2(@PathParam("Integer") Integer intParam, @PathParam("char") char charParam) {

        return Response.ok(intParam + "/" + charParam).build();
    }

    @POST
    @Path("/json/post")
    @Produces("application/json")
    @Consumes("application/json")
    public Dummy echoJsonPost(Dummy postParam) {

        postParam.name = "Received-" + postParam.name;
        postParam.value = "Received-" + postParam.value;

        return postParam;
    }

    @GET
    @Path("/context/path")
    @Produces("text/plain")
    public String getRoutePath(@Context HttpServerRequest request) {

        return request.absoluteURI();
    }

    @GET
    @Path("/context/null")
    @Produces("text/plain")
    public String getNull() {

        return null;
    }
}

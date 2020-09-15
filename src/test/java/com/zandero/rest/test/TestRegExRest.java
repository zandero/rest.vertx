package com.zandero.rest.test;

import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.data.RouteDefinition;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 *
 */
@Path("regEx")
public class TestRegExRest {

    @RouteOrder(20)
    @GET
    @Path("/{one:\\w+}/{two:\\d+}/{three:\\w+}")
    public Response A_oneTwoThree(@PathParam("two") int two, @PathParam("one") String one, @PathParam("three") String three) {

        Map<String, String> map = new HashMap<>();
        map.put("one", one);
        map.put("two", two + "");
        map.put("three", three);

        return Response.ok(map).build();
    }

    @RouteOrder(10)
    @GET
    @Path("/:one:\\d+")
    public Response B_test(int one) {
        return Response.ok(one).build();
    }

    @RouteOrder(15)
    @GET
    @Path("/:one:\\d+/minus/:two:\\d+")
    public Response C_test(int one, int two) {
        return Response.ok(one - two).build();
    }

    @RouteOrder(30)
    @GET
    @Path("{path:(?!api\\/).*}")
    public String D_allButApi(String path) {
        return path + " - not /api";
    }

    @RouteOrder(40)
    @GET
    @Path("{path:.*}")
    public String E_serveDocFile(@PathParam("path") String path) {
        return path + " - last";
    }

    @RouteOrder(50)
    @GET
    @Path("/1.0")
    public String F_notRegExPath(@Context RouteDefinition definition) {
        return definition.getPath() + " - notRegEx";
    }

    @RouteOrder(60)
    @GET
    @Path("/1.0/:version:1.3")
    public String G_isRegExPath(@PathParam("path") String version, @Context RouteDefinition definition) {
        return definition.getPath() + " " + version + " - asRegEx";
    }
}

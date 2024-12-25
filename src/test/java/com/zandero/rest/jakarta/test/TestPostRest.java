package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.reader.*;
import com.zandero.rest.test.json.*;
import com.zandero.rest.writer.*;
import jakarta.ws.rs.*;

/**
 *
 */
@Produces("application/json")
@Path("/post")
public class TestPostRest {

    @POST
    @Path("/json")
    @Consumes("application/json; charset=utf-8")
    @Produces("application/json; charset=utf-8")
    @RouteOrder(10)
    public Dummy echoJsonPost(Dummy postParam, @HeaderParam("X-Test") String testHeader) {

        postParam.name = "Received-" + postParam.name;
        postParam.value = "Received-" + postParam.value;

        return postParam;
    }

    @PUT
    @Path("/json")
    @RequestReader(DummyBodyReader.class)
    @ResponseWriter(TestDummyWriter.class)
    @RouteOrder(20)
    public Dummy echoJsonPut(Dummy postParam, @HeaderParam("X-Test") String testHeader) {

        postParam.name = "Received-" + postParam.name;
        postParam.value = "Received-" + postParam.value;

        return postParam;
    }

    @DELETE
    @Path("/json")
    @RequestReader(DummyBodyReader.class)
    @ResponseWriter(TestDummyWriter.class)
    @RouteOrder(20)
    public Dummy deleteWithJson(Dummy postParam, @HeaderParam("X-Test") String testHeader) {

        postParam.name = "Received-" + postParam.name;
        postParam.value = "Received-" + postParam.value;

        return postParam;
    }
}

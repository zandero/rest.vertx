package com.zandero.resttest.test;

import com.zandero.rest.annotation.*;
import com.zandero.resttest.reader.DummyBodyReader;
import com.zandero.resttest.test.json.Dummy;
import com.zandero.resttest.writer.TestDummyWriter;

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

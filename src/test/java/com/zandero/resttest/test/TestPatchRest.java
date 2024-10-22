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
@Path("/patch")
public class TestPatchRest {

    @PATCH
    @Path("/it")
    @RequestReader(DummyBodyReader.class)
    @ResponseWriter(TestDummyWriter.class)
    @RouteOrder(20)
    public Dummy echoJsonPatch(Dummy postParam) {

        postParam.name = "Received-" + postParam.name;
        postParam.value = "Received-" + postParam.value;

        return postParam;
    }
}

package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestDummyWriter;

import javax.ws.rs.*;

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

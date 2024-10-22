package com.zandero.resttest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.resttest.writer.TestDummyWriter;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("/incompatible")
public class TestIncompatibleWriterRest {

    @GET
    @Path("ouch")
    @ResponseWriter(TestDummyWriter.class)
    public String returnOuch() {

        return "should not work!";
    }
}

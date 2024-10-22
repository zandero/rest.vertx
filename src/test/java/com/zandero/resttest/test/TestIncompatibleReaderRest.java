package com.zandero.resttest.test;

import com.zandero.rest.annotation.RequestReader;
import com.zandero.resttest.reader.IntegerBodyReader;

import jakarta.ws.rs.*;

/**
 *
 */
@Path("/incompatible")
public class TestIncompatibleReaderRest {

    @POST
    @Path("ouch")
    @RequestReader(IntegerBodyReader.class)
    public String returnOuch(String bla) {

        return "should not work!";
    }
}

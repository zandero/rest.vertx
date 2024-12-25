package com.zandero.rest.jakarta.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.reader.*;
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

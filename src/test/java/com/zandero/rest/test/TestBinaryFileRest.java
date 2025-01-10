package com.zandero.rest.test;

import com.zandero.rest.annotation.*;
import com.zandero.rest.writer.*;
import com.zandero.utils.*;

import javax.ws.rs.*;

/**
 *
 */
@Path("bin")
public class TestBinaryFileRest {

    @GET
    @Path("/pdf")
    @ResponseWriter(BinaryResponseWriter.class)
    public byte[] serveDocFile() {
        return ResourceUtils.getBytes(this.getClass().getResourceAsStream("/dummy.pdf"));
    }
}

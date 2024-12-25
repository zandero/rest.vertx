package com.zandero.rest.jakarta.test;

import jakarta.ws.rs.*;

/**
 * At least one path ...
 */
public class TestMissingPathRest {

    @GET
    public String echo() {

        return "not possible";
    }
}

package com.zandero.resttest.test;

import com.zandero.resttest.annotation.AuditLog;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/system/user")
public class TestIssue55Rest {

    @DELETE
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    @AuditLog(title = "delete")
    public String query() {
        return "[delete]";
    }


    @GET
    @Path("/echo")
    @Produces(MediaType.TEXT_HTML)
    @AuditLog(title = "echo")
    public String echo2() {
        return "Hello echo";
    }

    @GET
    @Path("/echo2")
    @Produces(MediaType.TEXT_HTML)
    public String echo3() {
        return "Hello echo 2";
    }
}

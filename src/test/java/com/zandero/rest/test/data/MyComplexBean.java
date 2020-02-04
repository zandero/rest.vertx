package com.zandero.rest.test.data;

import com.zandero.rest.annotation.Raw;
import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

public class MyComplexBean {

    private boolean header;

    private String path;

    private int query;

    private String cookie;

    private Dummy context;

    public MyComplexBean(@PathParam("path") String path,
                         @HeaderParam("MyHeader") boolean header,
                         @QueryParam("query") @Raw int query,
                         @CookieParam("chocolate") String cookie,
                         @Context Dummy object) {

        this.path = path;
        this.header = header;
        this.query = query;
        this.cookie = cookie;
        this.context = object;
    }

    @Override
    public String toString() {
        return "Header: " + header +
                ", Path: " + path +
                ", Query: " + query +
                ", Cookie: " + cookie +
                ", Context: " + context.name + ", " + context.value;
    }
}

package com.zandero.rest.test.data;

import com.zandero.rest.annotation.Raw;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class MyComplexBean {

    private boolean header;

    private String path;

    private int query;

    private String cookie;

    public MyComplexBean(@PathParam("path") String path,
                         @HeaderParam("MyHeader") boolean header,
                         @QueryParam("query") @Raw int query,
                         @CookieParam("chocolate") String cookie) {

        this.path = path;
        this.header = header;
        this.query = query;
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "Header: " + header +
                ", Path: " + path +
                ", Query: " + query +
                ", Cookie: " + cookie;
    }
}

package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.writer.FileResponseWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Path("docs")
public class StaticFileRest {

	@GET
	@Path("/{path:.*}")
	@ResponseWriter(FileResponseWriter.class)
	public String serveDocFile(@PathParam("path") String path) {

		return "html/" + path;
	}
}

package com.zandero.rest.test;

import com.zandero.utils.StringUtils;
import io.vertx.ext.web.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.*;

@Path("/upload")
public class TestUploadFileRest {

    @POST
    @Path("/file")
    public String upload(@Context RoutingContext context) {

        Set<FileUpload> fileUploadSet = context.fileUploads();
        if (fileUploadSet == null || fileUploadSet.isEmpty()) {
            return "missing upload file!";
        }

        Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
        List<String> urlList = new ArrayList<>();
        while (fileUploadIterator.hasNext()) {
            FileUpload fileUpload = fileUploadIterator.next();
            urlList.add(fileUpload.uploadedFileName());
        }

        return StringUtils.join(urlList, ", ");
    }
}
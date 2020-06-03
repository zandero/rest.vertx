package com.zandero.rest.test;

import com.zandero.utils.StringUtils;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Path("/upload")
public class UploadFileRest {

    @POST
    @Path("/file")
    public String importData(@Context RoutingContext context) {

        Set<FileUpload> fileUploadSet = context.fileUploads();
        if (fileUploadSet == null || fileUploadSet.isEmpty()) {
            return "missing upload file!";
        }

        Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
        List<String> urlList = new ArrayList<>();
        while (fileUploadIterator.hasNext()) {
            FileUpload fileUpload = fileUploadIterator.next();

            // default folder is file-uploads in vertx
            // Could you give us method to change default upload folder?
            // Add BodyHandler.create().setUploadsDirectory("....") in your code.
            urlList.add(fileUpload.uploadedFileName());
        }

        return StringUtils.join(urlList, ", ");
    }
}
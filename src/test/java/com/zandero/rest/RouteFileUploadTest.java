package com.zandero.rest;

import com.zandero.rest.test.TestUploadFileRest;
import com.zandero.utils.ResourceUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.multipart.MultipartForm;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteFileUploadTest extends VertxTest {

    @BeforeAll
    static void start() throws URISyntaxException {

        before();

        BodyHandler bodyHandler = BodyHandler.create("my_upload_folder");
        RestBuilder builder = new RestBuilder(vertx)
                                  .bodyHandler(bodyHandler)
                                  .register(TestUploadFileRest.class);

        Router router = builder.build();
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void uploadFile(VertxTestContext context) {

        String resourceName = "/html/index.html";
        String path = ResourceUtils.getResourceAbsolutePath(resourceName);
        File file = new File(path);

        MultipartForm form = MultipartForm.create()
                                 .binaryFileUpload("file",
                                                   file.getName(),
                                                   file.getAbsolutePath(),
                                                   "text/plain");

        client.post(PORT, HOST, "/upload/file")
            .putHeader("ContentType", "multipart/form-data")
            .sendMultipartForm(form,
                               context.succeeding(response -> context.verify(() -> {
                                   assertEquals(200, response.statusCode());

                                   String fileName = response.bodyAsString();
                                   assertTrue(fileName.startsWith("my_upload_folder/"), fileName);

                                   Buffer uploaded = vertx.fileSystem().readFileBlocking(fileName);
                                   String compare = ResourceUtils.getResourceAsString(resourceName);
                                   assertEquals(compare, uploaded.toString());

                                   context.completeNow();
                               })));
    }
}

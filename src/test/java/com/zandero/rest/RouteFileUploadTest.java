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

        String path = new File(RouteFileUploadTest.class.getProtectionDomain().getCodeSource().getLocation()
                                   .toURI()).getPath();

        BodyHandler bodyHandler = BodyHandler.create("my_upload_folder");
        RestBuilder builder = new RestBuilder(vertx).bodyHandler(bodyHandler).register(TestUploadFileRest.class);

        //RestRouter.setBodyHandler(bodyHandler);

        Router router = builder.build();
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void uploadFile(VertxTestContext context) {

        String path = ResourceUtils.getResourceAbsolutePath("/html/index.html");
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
                                   String compare = ResourceUtils.getResourceAsString("/html/index.html");
                                   assertEquals(compare, uploaded.toString());

                                   context.completeNow();
                               })));
    }
}

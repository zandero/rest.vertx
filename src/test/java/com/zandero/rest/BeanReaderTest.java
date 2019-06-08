package com.zandero.rest;

import com.zandero.rest.test.TestBeanReaderRest;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxExtension;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

//@ExtendWith(VertxExtension.class)
@ExtendWith(VertxExtension.class)
public class BeanReaderTest extends VertxTest {

    @BeforeAll
    static void startUp() {

//        super.before();

        Router router = RestRouter.register(vertx, TestBeanReaderRest.class);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT);
    }

   /* @Test
    public void testCustomInput() {

        // call and check response
        //final Async async = context.async();

        Vertx vertx = Vertx.vertx();
        vertx.createHttpServer()
                .requestHandler(req -> req.response().end("Ok"))
                .listen(PORT, ar -> {
                    // (we can check here if the server started or not)
                });

        client.post("/read/bean").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

            assertEquals(200, response.statusCode());


                assertEquals("brown,dog,fox,jumps,over,quick,red,the", response.body()); // returns sorted list of unique words
                context.completeNow();
            });
        }).end("The quick brown fox jumps over the red dog!");
    }

    @Test
    public void testCustomInputNew(VertxTestContext context) {

        // call and check response


        FakeReadStream<Buffer> rs = new FakeReadStream<>();
*//*        FakeWriteStream<String> ws = new FakeWriteStream<>();
        ws.write("The quick brown fox jumps over the red dog!")
        Pump.pump(rs, ws);*//*

        *//*ReadStream<Buffer> readStream = new BufferReadStream(testBuffer);
        IntStream intStream = "The quick brown fox jumps over the red dog!".chars();*//*


        webClient.post(HOST, "/read/bean").sendStream(rs.as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

            context.assertTrue(response.succeeded(), response.cause().getMessage());
            HttpResponse<Buffer> buffer = response.result();

            assertEquals("brown,dog,fox,jumps,over,quick,red,the", buffer.bodyAsString()); // returns sorted list of unique words
        });
    }*/
}
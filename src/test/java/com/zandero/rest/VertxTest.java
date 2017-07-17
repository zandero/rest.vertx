package com.zandero.rest;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.unit.TestContext;
import org.junit.After;
import org.junit.Before;

/**
 *
 */
public class VertxTest {

	protected static final int PORT = 4444;

	private static final String HOST = "localhost";

	protected static final String ROOT_PATH = "http://" + HOST + ":" + PORT;

	protected Vertx vertx;

	protected HttpClient client;

	@Before
	public void before(TestContext context) {

		vertx = Vertx.vertx();
		client = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(HOST).setDefaultPort(PORT));

		RestRouter.globalErrorHandlers = null; // clear error handlers before each test
		RestRouter.globalErrorWriters = null; // clear error handlers before each test

		// clear all registered writers or reader
		RestRouter.getReaders().clear();
		RestRouter.getWriters().clear();
	}

	@After
	public void after(TestContext context) {

		vertx.close(context.asyncAssertSuccess());
	}
}

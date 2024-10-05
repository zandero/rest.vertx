open module rest.vertx.test {
    requires com.zandero.rest.vertx;
    requires io.vertx.core;
    requires io.vertx.web;
    requires io.vertx.testing.junit5;
    requires io.vertx.web.common;
    requires org.junit.jupiter.api;
    requires io.vertx.auth.common;
    requires io.vertx.web.client;
    requires jakarta.ws.rs;
    requires com.google.guice;
    requires utils.junit;
    requires jakarta.inject;
    //requires utils;
    requires com.fasterxml.jackson.databind;
    requires org.mockito.junit.jupiter;
    requires org.mockito;
    requires jakarta.validation;
    requires org.slf4j;
    requires aopalliance;
    requires org.hibernate.validator;
    requires jakarta.annotation;
}
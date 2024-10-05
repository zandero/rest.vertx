module com.zandero.rest.vertx {
    requires io.vertx.core;

    requires static io.vertx.web;
    requires static io.vertx.auth.common;

    requires jakarta.validation;
    requires jakarta.annotation;
    requires jakarta.ws.rs;
    requires org.slf4j;

    requires java.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.validator;


    exports com.zandero.rest;
    exports com.zandero.rest.authentication;
    exports com.zandero.rest.authorization;
    exports com.zandero.rest.exception;
    exports com.zandero.rest.context;
    exports com.zandero.rest.injection;
    exports com.zandero.rest.bean;
    exports com.zandero.rest.reader;
    exports com.zandero.rest.writer;


    exports com.zandero.rest.utils to rest.vertx.test;
    exports com.zandero.rest.data to rest.vertx.test;
    exports com.zandero.rest.utils.extra to rest.vertx.test;
    exports com.zandero.rest.annotation to rest.vertx.test;
    exports com.zandero.rest.cache to rest.vertx.test;
    exports com.zandero.rest.events;

}
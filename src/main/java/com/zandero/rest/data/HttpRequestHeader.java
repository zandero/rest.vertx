package com.zandero.rest.data;

import com.zandero.utils.Assert;

import java.util.Arrays;

public enum HttpRequestHeader {

    A_IM("A-IM"),
    Accept("Accept"),
    Accept_Charset("Accept-Charset"),
    Accept_Encoding("Accept-Encoding"),
    Accept_Language("Accept-Language"),
    Accept_Datetime("Accept-Datetime"),
    Access_Control_Request_Method("Access-Control-Request-Method"),
    Access_Control_Request_Headers("Access-Control-Request-Headers"),
    Authorization("Authorization"),
    Cache_Control("Cache-Control"),
    Connection("Connection"),
    Content_Length("Content-Length"),
    Content_Type("Content-Type"),
    Cookie("Cookie"),
    Date("Date"),
    Expect("Expect"),
    Forwarded("Forwarded"),
    From("From"),
    Host("Host"),
    If_Match("If-Match"),
    If_Modified_Since("If-Modified-Since"),
    If_None_Match("If-None-Match"),
    If_Range("If-Range"),
    If_Unmodified_Since("If-Unmodified-Since"),
    Max_Forwards("Max-Forwards"),
    Origin("Origin"),
    Pragma("Pragma"),
    Proxy_Authorization("Proxy-Authorization"),
    Range("Range"),
    Referer("Referer"),
    TE("TE"),
    User_Agent("User-Agent"),
    Upgrade("Upgrade"),
    Via("Via"),
    Warning("Warning"),

    Dnt("Dnt"),
    X_Requested_With("X-Requested-With"),
    X_CSRF_Token("X-CSRF-Token");

    public final String header;

    HttpRequestHeader(String name) {
        header = name;
    }

    public static boolean isConsumeHeader(String name) {
        return Accept.header.equalsIgnoreCase(name);
    }

    public static boolean isRequestHeader(String name) {
        Assert.notNullOrEmptyTrimmed(name, "Missing header name to compare against!");
        return Arrays.stream(HttpRequestHeader.values()).anyMatch(it -> it.header.equalsIgnoreCase(name));
    }
}

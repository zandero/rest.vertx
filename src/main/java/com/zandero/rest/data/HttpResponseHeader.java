package com.zandero.rest.data;

import com.zandero.utils.Assert;

import java.util.Arrays;

public enum HttpResponseHeader {

    Accept_Patch("Accept-Patch"),
    Accept_Ranges("Accept-Ranges"),
    Age("Age"),
    Allow("Allow"),
    Alt_Svc("Alt-Svc"),
    Cache_Control("Cache-Control"),
    Connection("Connection"),
    Content_Disposition("Content-Disposition"),
    Content_Encoding("Content-Encoding"),
    Content_Language("Content-Language"),
    Content_Length("Content-Length"),
    Content_Location("Content-Location"),
    Content_Range("Content-Range"),
    Content_Type("Content-Type"),
    Date("Date"),
    DeltaBase("Delta-Base"),
    ETag("ETag"),
    Expires("Expires"),
    IM("IM"),
    Last_Modified("Last-Modified"),
    Link("Link"),
    Location("Location"),
    Pragma("Pragma"),
    Proxy_Authenticate("Proxy-Authenticate"),
    Public_Key_Pins("Public-Key-Pins"),
    Retry_After("Retry-After"),
    Server("Server"),
    Set_Cookie("Set-Cookie"),
    Strict_Transport_Security("Strict-Transport-Security"),
    Trailer("Trailer"),
    Transfer_Encoding("Transfer-Encoding"),
    Tk("Tk"),
    Upgrade("Upgrade"),
    Vary("Vary"),
    Via("Via"),
    Warning("Warning"),
    WWW_Authenticate("WWW-Authenticate"),

    Content_Security_Policy("Content-Security-Policy"),
    Refresh("Refresh"),
    X_Powered_By("X-Powered-By"),
    X_Request_ID("X-Request-ID"),
    X_UA_Compatible("X-UA-Compatible"),
    X_XSS_Protection("X-XSS-Protection");

    public final String header;

    HttpResponseHeader(String name) {
        header = name;
    }

    public static boolean isProducesHeader(String name) {
        return Content_Type.header.equalsIgnoreCase(name);
    }

    public static boolean isResponseHeader(String name) {
        Assert.notNullOrEmptyTrimmed(name, "Missing header name to compare against!");
        return Arrays.stream(HttpResponseHeader.values()).anyMatch(it -> it.header.equalsIgnoreCase(name));
    }
}

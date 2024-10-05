package com.zandero.utils.extra;

import com.zandero.utils.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Utils to work with URIs
 */
public final class UrlUtils {

    private static final String UTF_8 = "UTF-8";

    private UrlUtils() {
        // hide constructor
    }

    /**
     * Combines root (domain) and path part to a correct URL
     *
     * @param rootUrl     domain
     * @param relativeUrl path
     * @return correctly composed URL
     */
    public static String composeUrl(String rootUrl, String relativeUrl) {

        if (rootUrl == null || rootUrl.trim().length() == 0) {
            throw new IllegalArgumentException("rootUrl must be given.");
        }

        URI root;
        try {
            if (rootUrl.endsWith("/")) {
                rootUrl = rootUrl.substring(0, rootUrl.length() - 1);
            }

            root = new URI(rootUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid root URL given.");
        }

        if (relativeUrl != null) {
            try {

                URI uri = new URI(relativeUrl);
                relativeUrl = uri.toString();

                if (uri.isAbsolute()) {
                    return relativeUrl;
                }

                if (!relativeUrl.startsWith("/")) {
                    relativeUrl = root.getPath() + "/" + relativeUrl;
                }
                else {
                    relativeUrl = root.getPath() + relativeUrl;
                }

                URI full = root.resolve(relativeUrl);
                String out = full.toString();

                if (out.endsWith("/")) {
                    out = out.substring(0, out.length() - 1);
                }

                return out;
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid relative URL given.");
            }
        }

        return root.toString();
    }

    /**
     * Composes a correct URL
     *
     * @param scheme scheme
     * @param domain domain
     * @param port   port
     * @return correctly composed URL
     */
    public static String composeUrl(String scheme, String domain, int port) {

        return composeUrl(scheme, domain, port, null, null);
    }

    /**
     * Composes a correct URL
     *
     * @param scheme scheme
     * @param domain domain
     * @param port   port
     * @param path   path
     * @return correctly composed URL
     */
    public static String composeUrl(String scheme, String domain, int port, String path) {

        return composeUrl(scheme, domain, port, path, null);
    }

    /**
     * Composes a correct URL
     *
     * @param scheme scheme
     * @param domain domain
     * @param port   port
     * @param path   path
     * @param query  query string (encoded values)
     * @return correctly composed URL
     */
    public static String composeUrl(String scheme, String domain, int port, String path, Map<String, String> query) {

        // clean up path ...
        if (StringUtils.isNullOrEmptyTrimmed(path)) {
            path = "";
        } else if (!path.startsWith("/") &&
                       !path.startsWith("?")) {
            path = "/" + path;
        }

        // add port if needed ...
        if (port > 0 && port != 80 && port != 443) {
            domain = domain + ":" + port;
        }

        return composeUrl(scheme + "://" + domain + path, query);
    }

    /**
     * Composes url from path and query
     *
     * @param path  to compose URI from
     * @param query map of name=value pairs
     * @return URL
     */
    public static String composeUrl(String path, Map<String, String> query) {
        // clean up ...
        if (path.endsWith("/") || path.endsWith("?") || path.endsWith("&")) {
            path = path.substring(0, path.length() - 1);
        }

        String queryString = composeQuery(query);
        if (queryString.length() > 1) {
            // path already contains some query ... continue
            queryString = (path.contains("?") ? "&" : "?") + queryString;
        }

        return path + queryString;
    }

    /**
     * Composes HTTP query from map and encodes if needed
     *
     * @param query map of name=value pairs
     * @return query in format name=value
     */
    public static String composeQuery(Map<String, String> query) {

        StringBuilder queryString = new StringBuilder();
        if (query != null && query.size() > 0) {

            for (String name : query.keySet()) {

                String value = query.get(name);

                if (!StringUtils.isNullOrEmptyTrimmed(name) &&
                        !StringUtils.isNullOrEmptyTrimmed(value)) {

                    if (queryString.length() > 1) {
                        queryString.append("&");
                    }

                    queryString.append(encodeQuery(name));
                    queryString.append("=");
                    queryString.append(encodeQuery(value));
                }
            }
        }

        return queryString.toString();
    }

    /**
     * Resolves domain name from given URL
     *
     * @param url to get domain name from
     * @return domain name or null if not resolved
     */
    public static String resolveDomain(String url) {

        if (StringUtils.isNullOrEmptyTrimmed(url)) {
            return null;
        }

        // check if url starts with scheme part (http:// ... ) if not add one manually
        if (!url.contains("://")) {
            url = "http://" + url;
        }

        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }


    /**
     * Resolves domain part of path (with scheme and port)
     *
     * @param path to get base from (scheme, port and domain)
     * @return scheme, domain and port of path
     */
    public static String getBaseUrl(String path) {

        if (!ValidatingUtils.isUrl(path)) {
            return null;
        }

        String scheme = resolveScheme(path);
        String domain = resolveDomain(path);
        Integer port = resolvePort(path);
        return composeUrl(scheme, domain, port == null ? 80 : port);
    }

    /**
     * Encode whole path
     *
     * @param path to be encoded
     * @return encoded path or null in case path is not a URL
     */
    public static String urlEncode(String path) {

        try {
            String decodedURL = URLDecoder.decode(path, UTF_8);
            URL url = new URL(decodedURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString().trim();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Resolved scheme from path
     *
     * @param path to get scheme
     * @return resolved scheme or null in case path is not a URL
     */
    public static String resolveScheme(String path) {

        try {
            URI uri = new URI(path);
            return uri.getScheme();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resolved port from URL
     *
     * @param path to get port from
     * @return port of 80 as default port, null if path is not a URL
     */
    public static Integer resolvePort(String path) {

        try {
            URI uri = new URI(path);
            int port = uri.getPort();

            return port >= 0 ? port : 80;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encodes query string UTF-8
     *
     * @param query string
     * @return encoded query string
     */
    public static String encodeQuery(String query) {

        if (StringUtils.isNullOrEmptyTrimmed(query)) {
            return query;
        }

        try {
            return URLEncoder.encode(query, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return query;
        }
    }

    /**
     * Parses query from given URL ...
     *
     * @param path to get query string parameters from
     * @return key value map of query strings, or empty map in case no query present
     */
    public static Map<String, String> getQuery(String path) {

        Map<String, String> query = new LinkedHashMap<>();

        if (StringUtils.isNullOrEmptyTrimmed(path)) {
            return query;
        }

        int minLength = 0;
        int index = path.indexOf("?");
        if (index >= 0) {
            path = path.substring(index + 1);
        } else if (ValidatingUtils.isUrl(path)) {
            minLength = 1;
        }

        String[] parts = path.split("&");
        if (parts.length > minLength) {

            for (String part : parts) {
                String[] nameValue = part.split("=");
                if (nameValue.length == 2) {
                    query.put(nameValue[0], nameValue[1]);
                }

                if (nameValue.length == 1) {
                    query.put(nameValue[0], null);
                }
            }
        }

        return query;
    }
}
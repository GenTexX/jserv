package com.schulz.jserv.http;

public enum JsrvHttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE;

    public static boolean isValid(String method) {
        try {
            JsrvHttpMethod.valueOf(method.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static JsrvHttpMethod fromString(String method) {
        if (isValid(method)) {
            return JsrvHttpMethod.valueOf(method.toUpperCase());
        }
        throw new IllegalArgumentException("Invalid HTTP method: " + method);
    }
    public String getMethod() {
        return this.name();
    }
}

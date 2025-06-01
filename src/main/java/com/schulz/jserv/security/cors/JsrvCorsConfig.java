package com.schulz.jserv.security.cors;

import java.util.List;
import java.util.Optional;

import com.schulz.jserv.http.JsrvHttpMethod;

public class JsrvCorsConfig {


    public static final String ENABLED_KEY = "jserv.cors.enabled"; 

    public static final String ORIGIN_KEY = "jserv.cors.origin"; 
    private String allowedOrigin;
    public static final String METHODS_KEY = "jserv.cors.methods";
    private Optional<List<JsrvHttpMethod>> allowedMethods;
    public static final String HEADERS_KEY = "jserv.cors.headers";
    private Optional<List<String>> allowedHeaders;

    public JsrvCorsConfig() {
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    public Optional<List<JsrvHttpMethod>>  getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<JsrvHttpMethod> allowedMethods) {
        this.allowedMethods = Optional.of(allowedMethods);
    }

    public Optional<List<String>> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = Optional.of(allowedHeaders);
    }
    
}

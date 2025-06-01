package com.schulz.jserv.core;

import com.schulz.jserv.http.JsrvUrl;
import com.schulz.jserv.security.cors.JsrvAuthHandle;

import io.undertow.server.HttpServerExchange;

public class JsrvExchange {

    private HttpServerExchange exchange;
    private JsrvAuthHandle authHandle;
    private String body;
    
    public JsrvExchange(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange getExchange() {
        return exchange;
    }

    public JsrvUrl getRequestUrl() {
        return JsrvUrl.parse(exchange.getRequestPath());
    }

    /******************/
    /* AUTHENTICATION */
    /******************/

    public void setAuthHandle(JsrvAuthHandle handle) {
        this.authHandle = handle;
    }

    public boolean isAuthenticated() {
        return this.authHandle != null && this.authHandle.isAuthenticated();
    }

    /******************/
    /*    RESPONSE    */
    /******************/

    public void setStatusCode(int statusCode) {
        this.exchange.setStatusCode(statusCode);
    }

    public void setResponseBody(String body) {
        this.body = body;
    }

    public String getResponseBody() {
        return body;
    }

}

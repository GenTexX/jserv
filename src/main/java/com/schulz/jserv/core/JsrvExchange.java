package com.schulz.jserv.core;

import java.util.function.Consumer;

import com.google.gson.Gson;
import com.schulz.jserv.http.JsrvUrl;
import com.schulz.jserv.security.jwt.JsrvAuthHandle;

import io.undertow.io.Receiver.FullBytesCallback;
import io.undertow.server.HttpServerExchange;

public class JsrvExchange {

    private HttpServerExchange exchange;
    private JsrvAuthHandle authHandle;
    private JsrvServer server;
    
    public JsrvExchange(JsrvServer server, HttpServerExchange exchange) {
        this.server = server;
        this.exchange = exchange;
    }

    public HttpServerExchange getExchange() {
        return exchange;
    }

    public JsrvServer getServer() {
        return server;
    }

    public JsrvUrl getRequestUrl() {
        return JsrvUrl.parse(exchange.getRequestPath());
    }

    public <T> void getRequestBody(Class<T> clazz, Consumer<T> consumer) {
        exchange.getRequestReceiver().receiveFullBytes(new FullBytesCallback() {
            @Override
            public void handle(HttpServerExchange exchange, byte[] bytes) {
                String requestBody = new String(bytes);

                T result = new Gson().fromJson(requestBody, clazz);

                consumer.accept(result);
            }
        });
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

}

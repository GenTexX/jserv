package com.schulz.jserv.core;

import io.undertow.server.HttpServerExchange;

public class JsrvExchange {

    private HttpServerExchange exchange;
    
    public JsrvExchange(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange getExchange() {
        return exchange;
    }
    
}

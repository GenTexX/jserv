package com.schulz.jserv.core.filter.impl;

import com.schulz.jserv.core.JsrvExchange;
import com.schulz.jserv.core.JsrvServer;
import com.schulz.jserv.core.filter.JsrvFilter;
import com.schulz.jserv.core.filter.JsrvFilterChain;
import com.schulz.jserv.http.JsrvHttpMethod;
import com.schulz.jserv.security.cors.JsrvCorsConfig;

import io.undertow.util.HttpString;

public class JsrvCorsFilter implements JsrvFilter {

    public static final String CORS_FILTER_KEY = "jsrv_core_cors_filter";
    
    private JsrvServer server;

    public JsrvCorsFilter(JsrvServer server) {
        this.server = server;
    }

    @Override
    public boolean handle(JsrvExchange exchange, JsrvFilterChain jsrvFilterChain) throws Exception {

        JsrvCorsConfig cors = this.server.getServerConfig().getCorsConfig();

        // Set CORS headers
        String allowedOrigin = cors.getAllowedOrigin();
        if (allowedOrigin != null && !allowedOrigin.isEmpty()) {
            exchange.getExchange().getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), allowedOrigin);
        }
        cors.getAllowedMethods().ifPresent(methods -> {
            String allowedMethods = String.join(", ", methods.stream().map(JsrvHttpMethod::name).toList());
            exchange.getExchange().getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), allowedMethods);
        });
        cors.getAllowedHeaders().ifPresent(headers -> {
            String allowedHeaders = String.join(", ", headers);
            exchange.getExchange().getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), allowedHeaders);
        });


        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getExchange().getRequestMethod().toString())) {
            exchange.getExchange().setStatusCode(204);
            return true; // End processing for preflight requests
        }

        return jsrvFilterChain.next(exchange);
    }

    @Override
    public String getKey() {
        return CORS_FILTER_KEY;
    }

}

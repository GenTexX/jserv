package com.schulz.jserv.core;

import com.schulz.jserv.core.dispatcher.JsrvDispatcher;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class JsrvHttpHandler implements HttpHandler {

    private JsrvServer server;

    JsrvHttpHandler(JsrvServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        
        // TODO: Filtering

        String[] pathSegments = exchange.getRequestPath().split("/");
        JsrvExchange jsrvExchange = new JsrvExchange(exchange);

        if (pathSegments.length > 1) {
            String dispatcherName = pathSegments[1];
            if (dispatcherName.isEmpty()) {
                dispatcherName = ""; // Default dispatcher
            }
            dispatchRequest(dispatcherName, jsrvExchange);
        } else {
            String dispatcherName = ""; 
            dispatchRequest(dispatcherName, jsrvExchange);
        }

    }

    private void dispatchRequest(String dispatcherName, JsrvExchange exchange) {
        JsrvDispatcher dispatcher = server.getDispatcher(dispatcherName);
        if (dispatcher != null) {
            if (!dispatcher.onRequest(exchange)) {
                exchange.getExchange().setStatusCode(404);
                exchange.getExchange().getResponseSender().send("Not Found");
            }
        } else {
            exchange.getExchange().setStatusCode(404);
            exchange.getExchange().getResponseSender().send("site not found");
        }
    }
    
}

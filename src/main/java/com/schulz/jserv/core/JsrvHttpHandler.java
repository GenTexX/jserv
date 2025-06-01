package com.schulz.jserv.core;

import java.util.List;

import com.schulz.jserv.core.dispatcher.JsrvDispatcher;
import com.schulz.jserv.core.filter.JsrvFilterChain;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class JsrvHttpHandler implements HttpHandler {

    private JsrvServer server;

    JsrvHttpHandler(JsrvServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        
        String path = exchange.getRequestPath();
        JsrvExchange jsrvExchange = new JsrvExchange(exchange);
        dispatchRequest(path, jsrvExchange);

    }

    private void dispatchRequest(String path, JsrvExchange exchange) throws Exception {
        List<JsrvDispatcher> dispatchers = server.getDispatcher();

        List<JsrvDispatcher> matchingDispatchers = dispatchers.stream()
                .filter(d -> d.getBasePath().match(path))
                .toList();

        if (matchingDispatchers.isEmpty()) {
            exchange.getExchange().setStatusCode(404);
            exchange.getExchange().getResponseSender().send("site not found");
            return;
        }
        
        JsrvDispatcher dispatcher = matchingDispatchers.get(0);

        JsrvFilterChain chain = new JsrvFilterChain(this.server.getFilters(), dispatcher);
        if (dispatcher != null) {
            if (!chain.next(exchange)) {
                exchange.getExchange().setStatusCode(404);
                exchange.getExchange().getResponseSender().send("site not found");
            } else {
                exchange.getExchange().getResponseSender().send(exchange.getResponseBody());
            }
        } else {
            exchange.getExchange().setStatusCode(404);
            exchange.getExchange().getResponseSender().send("site not found");
        }
    }
    
}

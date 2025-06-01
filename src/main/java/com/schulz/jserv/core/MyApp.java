package com.schulz.jserv.core;

import com.schulz.jserv.core.dispatcher.JsrvDispatcher;
import com.schulz.jserv.http.JsrvUrl;

public class MyApp {
    
    public static void main(String[] args) throws Exception {

        // Create and start the server
        JsrvServer server = new JsrvServer();

        server.addDispatcher(new JsrvDispatcher() {
            @Override
            public boolean onRequest(JsrvExchange exchange) {
                // Handle the request here
                exchange.getExchange().getResponseSender().send("jserv!");
                return true;
            }

            @Override
            public JsrvUrl getBasePath() {
                return JsrvUrl.parse("/");
            }
        });

        server.addDispatcher(new JsrvDispatcher() {
            @Override
            public boolean onRequest(JsrvExchange exchange) {
                // Handle the request here
                exchange.getExchange().getResponseSender().send("Hello from another Dispatcher!");
                return true;
            }

            @Override
            public JsrvUrl getBasePath() {
                return JsrvUrl.parse("/hello");
            }
        });

        server.start();

    }
}

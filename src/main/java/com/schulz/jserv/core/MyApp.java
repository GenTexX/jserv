package com.schulz.jserv.core;

import com.schulz.jserv.core.dispatcher.JsrvDispatcher;

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
            public String getBasePath() {
                return "";
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
            public String getBasePath() {
                return "hello";
            }
        });

        server.start();

    }
}

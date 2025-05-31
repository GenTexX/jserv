package com.schulz.jserv.core.dispatcher;

import com.schulz.jserv.core.JsrvExchange;

public interface JsrvDispatcher {
    
    /**
     * Handles the request and returns true if the request was handled.
     *
     * @param exchange The JsrvExchange object containing the request and response.
     * @return true if the request was handled, false otherwise.
     */
    boolean onRequest(JsrvExchange exchange);

    /**
     * Returns the base path for the dispatcher.
     *
     * @return The base path as a String.
     */
    String getBasePath();

}

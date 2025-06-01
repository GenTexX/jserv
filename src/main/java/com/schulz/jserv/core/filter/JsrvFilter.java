package com.schulz.jserv.core.filter;

import com.schulz.jserv.core.JsrvExchange;

public interface JsrvFilter {

    /**
     * Handles the request and response exchange.
     *
     * @param exchange the JsrvExchange containing request and response data
     * @param jsrvFilterChain the filter chain to continue processing
     * @return true if the request was handled, false to continue with the next filter
     * @throws Exception if an error occurs during processing
     */
    boolean handle(JsrvExchange exchange, JsrvFilterChain jsrvFilterChain) throws Exception;

    /**
     * Returns a unique key for this filter.
     *
     * @return the key as a String
     */
    String getKey();
    
}

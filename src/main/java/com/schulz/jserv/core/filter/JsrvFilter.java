package com.schulz.jserv.core.filter;

import com.schulz.jserv.core.JsrvExchange;

public interface JsrvFilter {

    boolean handle(JsrvExchange exchange, JsrvFilterChain jsrvFilterChain) throws Exception;
    
}

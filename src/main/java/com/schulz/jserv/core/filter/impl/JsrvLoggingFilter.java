package com.schulz.jserv.core.filter.impl;

import java.util.Deque;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.schulz.jserv.core.JsrvExchange;
import com.schulz.jserv.core.filter.JsrvFilter;
import com.schulz.jserv.core.filter.JsrvFilterChain;

import io.undertow.util.HttpString;

public class JsrvLoggingFilter implements JsrvFilter {

    private static final Logger logger = LoggerFactory.getLogger(JsrvLoggingFilter.class);

    @Override
    public boolean handle(JsrvExchange exchange, JsrvFilterChain jsrvFilterChain) throws Exception {
        
        String path = exchange.getExchange().getRequestPath();
        HttpString method = exchange.getExchange().getRequestMethod();
        Map<String, Deque<String>> qparam = exchange.getExchange().getQueryParameters();
        StringBuilder params = new StringBuilder();

        if (qparam != null && !qparam.isEmpty()) {
            qparam.forEach((key, value) -> {
                if (params.length() > 0) {
                    params.append("&");
                }
                params.append(key).append("=").append(String.join(",", value));
            });
        }

        logger.info("Request received: {} {}?{}", method, path, params.toString());
        return jsrvFilterChain.next(exchange);
        
    }
    
}

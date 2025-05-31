package com.schulz.jserv.core.filter;

import java.util.List;

import com.schulz.jserv.core.JsrvExchange;
import com.schulz.jserv.core.dispatcher.JsrvDispatcher;

public class JsrvFilterChain {

    private final List<JsrvFilter> filters;
    private final JsrvDispatcher finalHandler;
    private int index = 0;

    public JsrvFilterChain(List<JsrvFilter> filters, JsrvDispatcher finalHandler) {
        this.filters = filters;
        this.finalHandler = finalHandler;
    }

    public boolean next(JsrvExchange response) throws Exception {
        if (index < filters.size()) {
            JsrvFilter filter = filters.get(index++);
            return filter.handle(response, this);
        } else {
            return finalHandler.onRequest(response);
        }
    }
    
} 
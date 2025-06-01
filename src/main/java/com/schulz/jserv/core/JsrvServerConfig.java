package com.schulz.jserv.core;

import com.schulz.jserv.security.cors.JsrvCorsConfig;

public class JsrvServerConfig {
 
    public static final String HOST_KEY = "jserv.server.host";
    private String host;
    public static final String PORT_KEY = "jserv.server.port";
    private int port;

    private final JsrvCorsConfig corsConfig;

    public JsrvServerConfig(String host, int port, JsrvCorsConfig corsConfig) {
        this.host = host;
        this.port = port;
        this.corsConfig = corsConfig;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public JsrvCorsConfig getCorsConfig() {
        return corsConfig;
    }

}
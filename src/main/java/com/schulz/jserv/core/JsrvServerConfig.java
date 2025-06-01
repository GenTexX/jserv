package com.schulz.jserv.core;

import com.schulz.jserv.security.cors.JsrvCorsConfig;
import com.schulz.jserv.security.jwt.JsrvJWTConfig;

public class JsrvServerConfig {
 
    public static final String HOST_KEY = "jserv.server.host";
    private String host;
    public static final String PORT_KEY = "jserv.server.port";
    private int port;

    private final JsrvCorsConfig corsConfig;
    private final JsrvJWTConfig jwtConfig;

    public JsrvServerConfig(String host, int port, JsrvCorsConfig corsConfig, JsrvJWTConfig jwtConfig) {
        this.host = host;
        this.port = port;
        this.corsConfig = corsConfig;
        this.jwtConfig = jwtConfig;
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

    public JsrvJWTConfig getJwtConfig() {
        return jwtConfig;
    }

}
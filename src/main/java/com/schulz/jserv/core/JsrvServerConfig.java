package com.schulz.jserv.core;

public class JsrvServerConfig {
 
    public static final String HOST_KEY = "jserv.server.host";
    private String host;
    public static final String PORT_KEY = "jserv.server.port";
    private int port;

    public JsrvServerConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
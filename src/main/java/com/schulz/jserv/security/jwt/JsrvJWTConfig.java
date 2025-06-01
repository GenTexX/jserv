package com.schulz.jserv.security.jwt;

public class JsrvJWTConfig {
    
    public static final String SECRET_KEY = "jsrv.server.jwt.secret";
    private String jwtSecret;

    public static final String EXPIRATION_TIME_KEY = "jsrv.server.jwt.expiration";
    private int jwtExpirationTime;

    public JsrvJWTConfig(String jwtSecret, int jwtExpirationTime) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationTime = jwtExpirationTime;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtExpirationTime() {
        return jwtExpirationTime;
    }

}

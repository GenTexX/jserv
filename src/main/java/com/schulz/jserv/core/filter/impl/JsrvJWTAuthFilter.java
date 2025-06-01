package com.schulz.jserv.core.filter.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.schulz.jserv.core.JsrvApplicationConfig;
import com.schulz.jserv.core.JsrvExchange;
import com.schulz.jserv.core.JsrvServer;
import com.schulz.jserv.core.filter.JsrvFilter;
import com.schulz.jserv.core.filter.JsrvFilterChain;
import com.schulz.jserv.security.cors.JsrvAuthHandle;
import com.schulz.jserv.security.cors.JsrvAuthHandle.JsrvNotAuthenticatedHandle;

public abstract class JsrvJWTAuthFilter implements JsrvFilter {

    private static final Logger logger = LoggerFactory.getLogger(JsrvJWTAuthFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String JWT_AUTH_FILTER_KEY = "jsrv_core_jwt_auth_filter";
    public static final String JWT_SECRET_KEY = "jsrv.jwt.secret";

    private String secret;
    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    public JsrvJWTAuthFilter(JsrvServer server) {
        JsrvApplicationConfig.getValue(JWT_SECRET_KEY);
        if (this.secret == null || this.secret.isEmpty()) {
            throw new IllegalArgumentException("JWT secret must be provided in server configuration");
        }
        this.algorithm = Algorithm.HMAC256(this.secret);
        this.jwtVerifier = JWT.require(algorithm).build();
    }

    protected abstract JsrvAuthHandle authenticate(String subject);

    @Override
    public boolean handle(JsrvExchange exchange, JsrvFilterChain jsrvFilterChain) throws Exception {
        String authHeader = exchange.getExchange().getRequestHeaders().getFirst(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            try {
                DecodedJWT decodedJWT = jwtVerifier.verify(token);

                JsrvAuthHandle handle = authenticate(decodedJWT.getSubject());
                if (handle != null) {
                    // Set the authentication handle in the exchange for further processing
                    exchange.setAuthHandle(handle);
                } else {
                    logger.warn("Invalid JWT token provided: {}", token);
                    // Invalid token, set unauthorized status
                    exchange.getExchange().setStatusCode(401); // Unauthorized
                    return true; // Stop processing
                }

            } catch (JWTVerificationException e) {
                logger.warn("JWT verification failed: {}", e.getMessage());
                exchange.getExchange().setStatusCode(401); // Unauthorized
                return true; // Stop processing
            }

        } else {
            // No valid token provided
            exchange.setAuthHandle(JsrvNotAuthenticatedHandle.getInstance());
        }

        return jsrvFilterChain.next(exchange); // Continue with the next filter
    }

    @Override
    public String getKey() {
        return JWT_AUTH_FILTER_KEY;
    }

}

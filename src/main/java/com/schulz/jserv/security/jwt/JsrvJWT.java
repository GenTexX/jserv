package com.schulz.jserv.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JsrvJWT {

    public static Algorithm geAlgorithm(String secret) {
        return Algorithm.HMAC256(secret);
    }

    public static String createToken(String username, Algorithm algorithm, int jwtExpirationTime) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + jwtExpirationTime * 1000))
                .sign(algorithm);
    }



}

package com.plantingio.server.Utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
@Service
public class JwtUtil {

    private final String SECRET_KEY = "9uah2PtO8w2uFqpFOFyc5LSuV4DJ1cEal+QH84FUhLY=";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String email) {
        String token = JWT.create()
                .withSubject(email)
                .sign(algorithm);

        return token;
    }

    public String getSubject(String token) {
        try{
            JWTVerifier verifier = JWT.require(algorithm).build();

        } catch (JWTVerificationException e) {
            throw new IllegalArgumentException("Invalid JWT");
        }
        DecodedJWT jwt = JWT.decode(token);
        String subject = jwt.getSubject();
        return subject;
    }

    public String getEmail (String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());
        return this.getSubject(token);
    }

}

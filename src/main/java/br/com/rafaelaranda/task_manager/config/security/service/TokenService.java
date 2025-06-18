package br.com.rafaelaranda.task_manager.config.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UserDetails user) {
        LOGGER.info("Generating token for user: {}", user.getUsername());
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("task-manager-auth")
                    .withSubject(user.getUsername())
                    .withExpiresAt(getExpirationDateTime())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            LOGGER.error("Error while generating token: {}", e.getMessage(), e);
            throw new RuntimeException("Error while generating token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("task-manager-auth")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            LOGGER.warn("Token validation failed: {}", e.getMessage());
            return null;
        }
    }

    private Instant getExpirationDateTime() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC);
    }
}

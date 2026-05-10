package com.cloudops.gatewayservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewaySecurityTest {

    @LocalServerPort
    private int port;

    @Value("${security.jwt.secret}")
    private String secret;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRejectTaskRouteWithoutToken() {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/tasks")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldPassAuthForValidToken() {
        String token = generateToken("ravi");

        webTestClient.get()
                .uri("http://localhost:" + port + "/api/tasks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(15, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }
}

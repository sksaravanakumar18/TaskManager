package com.cloudops.authservice.service;

import com.cloudops.authservice.config.JwtProperties;
import com.cloudops.authservice.dto.TokenRequest;
import com.cloudops.authservice.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse issueToken(TokenRequest request) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.expirationMinutes(), ChronoUnit.MINUTES);
        List<String> roles = request.getRoles() == null || request.getRoles().isEmpty()
                ? List.of("USER")
                : request.getRoles();

        String token = Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(request.getUsername())
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(expiresAt))
                .claims(Map.of("roles", roles))
                .signWith(signingKey)
                .compact();

        return new TokenResponse(token, "Bearer", expiresAt);
    }

    public Claims validateAndParse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

package com.cloudops.authservice.controller;

import com.cloudops.authservice.dto.TokenRequest;
import com.cloudops.authservice.dto.TokenResponse;
import com.cloudops.authservice.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse issueToken(@Valid @RequestBody TokenRequest request) {
        return jwtService.issueToken(request);
    }

    @GetMapping("/validate")
    public Map<String, Object> validate(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        Claims claims = jwtService.validateAndParse(token);

        Map<String, Object> response = new HashMap<>();
        response.put("subject", claims.getSubject());
        response.put("issuer", claims.getIssuer());
        response.put("roles", claims.get("roles"));
        response.put("expiresAt", claims.getExpiration());
        return response;
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must be Bearer token");
        }
        return authorizationHeader.substring(7);
    }
}

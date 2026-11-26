package com.project.back_end.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject, String role, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        String cleanToken = stripBearerPrefix(token);
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(cleanToken)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Number userId = extractAllClaims(token).get("userId", Number.class);
        return userId != null ? userId.longValue() : null;
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration() != null && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isTokenValidForRole(String token, String expectedRole) {
        if (!isTokenValid(token)) {
            return false;
        }
        String role = extractRole(token);
        return role != null && role.equalsIgnoreCase(expectedRole);
    }

    private String stripBearerPrefix(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }
}

package com.example.booklibrary.security.jwt;

import com.example.booklibrary.security.dto.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtProvider {

    @Value("#{systemProperties['jwt.secret'] ?: '${jwt.secret}'}")
    private String base64EncodedJwtSecret;

    @Value("#{systemProperties['jwt.expiration'] ?: '${jwt.expiration}'}")
    private long jwtExpiration;

    private Key key;

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("role", Objects.requireNonNull(role))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .serializeToJsonWith(new JacksonSerializer<>())
                .compact();
    }

    public Pair<String, String> validateTokenAndGetSubject(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .deserializeJsonWith(new JacksonDeserializer<>())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            return Pair.of(username, role);
        } catch (Exception e) {
            return null;
        }
    }

    @PostConstruct
    private void init() {
        byte[] decodedKey = Base64.getDecoder().decode(base64EncodedJwtSecret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }
}



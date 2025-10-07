package com.liang.blog.util;

import com.liang.blog.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private final Key signingKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = buildSigningKey(properties.getSecret());
    }

    public String generateToken(Long userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getAccessTokenTtl());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<Long> parseUserId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            var parserBuilder = Jwts.parserBuilder().setSigningKey(signingKey);
            if (properties.getIssuer() != null && !properties.getIssuer().isBlank()) {
                parserBuilder.requireIssuer(properties.getIssuer());
            }
            Claims claims = parserBuilder.build().parseClaimsJws(token).getBody();
            String subject = claims.getSubject();
            return Optional.ofNullable(subject).map(Long::valueOf);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Key buildSigningKey(String secret) {
        byte[] keyBytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

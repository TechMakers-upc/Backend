package com.techmakers.fixcore.iam.infrastructure.tokens.jwt;

import com.techmakers.fixcore.iam.application.outboundservices.tokens.TokenService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService implements TokenService {

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtTokenService(@Value("${authorization.jwt.secret}") String secret,
                           @Value("${authorization.jwt.expiration.days}") long expirationDays) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofDays(expirationDays);
    }

    @Override
    public String generateToken(String subject) {
        var issuedAt = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String getSubjectFromToken(String token) {
        return Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            getSubjectFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}

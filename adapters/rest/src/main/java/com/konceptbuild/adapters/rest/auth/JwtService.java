package com.konceptbuild.adapters.rest.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(1);
    private final SecretKey signingKey;
    private final Clock clock;

    public JwtService() {
        this(Clock.systemUTC());
    }

    JwtService(Clock clock) {
        this.clock = clock;
        byte[] generatedSecret = new byte[48];
        new SecureRandom().nextBytes(generatedSecret);
        this.signingKey = Keys.hmacShaKeyFor(generatedSecret);
        log.info("Generated an in-memory JWT signing key; tokens will be invalidated when the application restarts.");
    }

    public String createToken(String subject) {
        Instant now = clock.instant();
        return Jwts.builder().subject(subject).id(UUID.randomUUID().toString()).issuedAt(Date.from(now)).expiration(Date.from(now.plus(TOKEN_EXPIRATION))).signWith(signingKey).compact();
    }

    public String getSubject(String token) {
        return parse(token).subject();
    }

    public TokenDetails parse(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
        return new TokenDetails(claims.getSubject(), UUID.fromString(claims.getId()),
                claims.getExpiration().toInstant());
    }

    public long getExpirationSeconds() {
        return TOKEN_EXPIRATION.toSeconds();
    }

    public record TokenDetails(String subject, UUID id, Instant expiresAt) {
    }
}

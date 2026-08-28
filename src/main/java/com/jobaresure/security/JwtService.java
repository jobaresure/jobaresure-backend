package com.jobaresure.security;

import com.jobaresure.config.properties.JwtProperties;
import com.jobaresure.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Issues and validates stateless JWTs (HS256). Two token types are produced:
 * a short-lived access token and a long-lived refresh token (distinguished by
 * the {@code type} claim).
 */
@Service
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "type";

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return buildToken(user, TYPE_ACCESS, props.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, TYPE_REFRESH, props.getRefreshTokenExpirationMs());
    }

    public long getAccessTokenExpirationMs() {
        return props.getAccessTokenExpirationMs();
    }

    private String buildToken(User user, String type, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(props.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .claims(Map.of(
                        CLAIM_ROLE, user.getRole().name(),
                        CLAIM_EMAIL, user.getEmail(),
                        CLAIM_TYPE, type))
                .signWith(key)
                .compact();
    }

    /** Parses and verifies a token's signature & expiry, returning its claims. */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String extractType(Claims claims) {
        return claims.get(CLAIM_TYPE, String.class);
    }
}

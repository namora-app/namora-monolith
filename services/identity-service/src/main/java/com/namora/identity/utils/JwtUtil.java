package com.namora.identity.utils;

import com.namora.identity.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${auth.ACCESS_TOKEN_SECRET}")
    private String accessTokenSecret;

    @Value("${auth.REFRESH_TOKEN_SECRET}")
    private String refreshTokenSecret;

    @Value("${auth.ACCESS_TOKEN_EXPIRATION}")
    private Long accessTokenExpiration;

    @Value("${auth.REFRESH_TOKEN_EXPIRATION}")
    private Long refreshTokenExpiration;

    private Key getAccessTokenSigningKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Key getRefreshTokenSigningKey() {
        return Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("email", userDetails.getAuthIdentity().getEmail());
        claims.put("userId", userDetails.getAuthIdentity().getId());
        claims.put("role", userDetails.getAuthIdentity().getRole());
        return Jwts.builder()
                .subject(userDetails.getAuthIdentity().getId())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessTokenSigningKey())
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("email", userDetails.getAuthIdentity().getEmail());
        return Jwts.builder()
                .subject(userDetails.getAuthIdentity().getId())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getRefreshTokenSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getAccessTokenSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            try {
                return Jwts.parser()
                        .verifyWith((SecretKey) getRefreshTokenSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            } catch (JwtException ex) {
                throw new JwtException("Invalid Refresh Token");
            }
        }
    }


    private String getType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String getEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    private boolean isAccessTokenExpired(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        assert exp != null;
        return exp.before(new Date());
    }

    private boolean isRefreshTokenExpired(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        assert exp != null;
        return exp.before(new Date());
    }

    public boolean isValidAccessToken(String token, CustomUserDetails userDetails) {
        final String email = getEmail(token);
        return email.equals(userDetails.getAuthIdentity().getEmail()) &&
                getType(token).equals("access") &&
                !isAccessTokenExpired(token);
    }

    public boolean isValidRefreshToken(String token, CustomUserDetails userDetails) {
        final String email = getEmail(token);
        return !isRefreshTokenExpired(token) &&
                getType(token).equals("refresh") && email.equalsIgnoreCase(userDetails.getAuthIdentity().getEmail());
    }

}
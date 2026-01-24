package com.namora.gateway.utils;

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
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${auth.ACCESS_TOKEN_SECRET}")
    private String accessTokenSecret;

    private String getType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Key getAccessTokenSigningKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getAccessTokenSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtException("Invalid Refresh Token");
        }
    }

    public String getRole(String token) {
        return extractClaim(token, claims -> claims.get("role").toString());
    }

    public String getUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId").toString());
    }

    private boolean isAccessTokenExpired(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        assert exp != null;
        return exp.before(new Date());
    }

    public boolean isValidAccessToken(String token) {
        return getType(token).equals("access") &&
                !isAccessTokenExpired(token);
    }

}

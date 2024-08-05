package com.banson.heathtagram.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 2;      // 2분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 4;     // 4분

    private String key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        key = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String email) {

        return Jwts.builder()
                .claim("email", email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String getAccessTokenInfo(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("email") == null) {
            throw new RuntimeException("정보없음");
        }

        return (String) claims.get("email");
    }

    public boolean validateToken(String accessToken) {
        try {
            if (!parseClaims(accessToken).getExpiration().before(new Date())) {
                return true;
            }
            return false;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
        }
        return false;
    }



    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }



}

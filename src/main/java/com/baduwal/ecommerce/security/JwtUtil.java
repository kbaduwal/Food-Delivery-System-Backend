package com.baduwal.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    public String generateJwtToken(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256,jwtSecret)
                .compact();
    }

    public Claims getUserNameFromJwtToken(String jwtToken) {
        return Jwts.parser().
                setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}

package com.elharche.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.expirationDate}")
    private long expirationDate;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        jwtParser = Jwts.parser().setSigningKey(getSigningKey()).build();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationDate))
                .claim("authorities", userDetails.getAuthorities())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String authToken) {
        return extractClaim(authToken, Claims::getSubject);
    }

    private <T> T extractClaim(String authToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(authToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String authToken) {
        return jwtParser.parseSignedClaims(authToken).getPayload();
    }

    public boolean validateToken(String authToken, UserDetails userDetails) {
        final String username = extractEmail(authToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(authToken));
    }

    private boolean isTokenExpired(String authToken) {
        return extractExpiration(authToken).before(new Date());
    }

    private Date extractExpiration(String authToken) {
        return extractClaim(authToken, Claims::getExpiration);
    }
}
package com.jacob.flowtrack.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
         return Keys.hmacShaKeyFor(secret.getBytes());
     }
     public String generateToken(String username) {

         return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

     }

     public String extractUsername(String token) {
         return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
     }

     public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

         return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
     }

     private boolean isTokenExpired(String token) {

        Date expiration = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getExpiration();

        return expiration.before(new Date());
     }

}
package com.sony.todoapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    // Secret key (must be at least 256 bits for HS256)
    private static final String SECRET = "MySuperSecretKeyMySuperSecretKey123!";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Token expiration time (1 day)
    private static final long EXPIRATION = 86400000L;

    // Generate a JWT token for a given user ID
    public static String generateToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Extract userId from a JWT token
    public static String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Check if the token is expired
    public static boolean isTokenExpired(String token) {
        Jws<Claims> parsedJws = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);

        return parsedJws.getBody().getExpiration().before(new Date());
    }
}

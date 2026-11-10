package com.springboot.FastX_backend.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtility {
    // we can encode the username with tools but not the password without this secret key ,
    // so this key is very imp and secured by devops engineer in vault
    @Value("${jwt.secret}")
    private String secretKey ;

    // Method to generate a JWT token for the given username
    public String generateToken(String username) { //We use this
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    //method to create Token
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()       //is used to start building a JWT token in Java.
                .setClaims(claims)
                .setSubject(username)//for which username this token is for
                .setIssuedAt(new Date(System.currentTimeMillis()))//token generated time
                .setExpiration(new Date(System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000))// this is the time limit in which the token would expire
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secretKey.getBytes()))// assign algo, 256/4 which is we get 64 alphanumeric token generated
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }
}

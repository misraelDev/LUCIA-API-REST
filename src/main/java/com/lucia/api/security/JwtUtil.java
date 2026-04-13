package com.lucia.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForJwtTokenGeneration123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private Long expiration;

        /**
         * Verifica el token JWT y retorna los datos del usuario extraídos del token.
         * Si el token es inválido o expirado, retorna null.
         */
        public com.lucia.api.model.dto.User.AuthResponse verifyUserToken(String token) {
            try {
                Claims claims = extractAllClaims(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                
                // Si el rol no está en el token (tokens antiguos), retornar null para forzar re-autenticación
                if (role == null || role.isEmpty()) {
                    return null;
                }
                
                Long expiresIn = claims.getExpiration() != null ? 
                    (claims.getExpiration().getTime() - System.currentTimeMillis()) : null;

                com.lucia.api.model.dto.User.AuthResponse response = new com.lucia.api.model.dto.User.AuthResponse();
                response.setRole(role);
                response.setExpiresIn(expiresIn);
                response.setAccessToken(token);
                return response;
            } catch (Exception e) {
                // Token inválido o expirado
                return null;
            }
        }
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("email", username);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public Long getExpiration() {
        return expiration;
    }
}
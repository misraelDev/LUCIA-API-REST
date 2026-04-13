package com.lucia.api.service.Cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lucia.api.model.dto.User.AuthResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TokenCacheService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCacheService.class);
    
    // Cache con TTL de 5 minutos
    private static final long CACHE_TTL_MINUTES = 5;
    
    // Cache thread-safe
    private final ConcurrentHashMap<String, CacheEntry> tokenCache = new ConcurrentHashMap<>();
    
    // Blacklist de tokens invalidados (logout)
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    // Marca por usuario: cualquier token emitido antes de este timestamp queda inválido.
    private final ConcurrentHashMap<String, Long> userSessionInvalidAfter = new ConcurrentHashMap<>();
    
    private static class CacheEntry {
        private final AuthResponse authResponse;
        private final long timestamp;
        
        public CacheEntry(AuthResponse authResponse) {
            this.authResponse = authResponse;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > TimeUnit.MINUTES.toMillis(CACHE_TTL_MINUTES);
        }
        
        public AuthResponse getAuthResponse() {
            return authResponse;
        }
    }
    
    /**
     * Obtiene la respuesta de autenticación del cache o null si no existe o está expirada
     */
    public AuthResponse getCachedAuthResponse(String token) {
        CacheEntry entry = tokenCache.get(token);
        if (entry == null) {
            return null;
        }
        
        if (entry.isExpired()) {
            tokenCache.remove(token);
            logger.debug("Token cache expired for token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            return null;
        }
        
        logger.debug("Token cache hit for token: {}", token.substring(0, Math.min(10, token.length())) + "...");
        return entry.getAuthResponse();
    }
    
    /**
     * Almacena la respuesta de autenticación en el cache
     */
    public void cacheAuthResponse(String token, AuthResponse authResponse) {
        if (authResponse != null) {
            tokenCache.put(token, new CacheEntry(authResponse));
            logger.debug("Token cached for token: {}", token.substring(0, Math.min(10, token.length())) + "...");
        }
    }
    
    /**
     * Limpia el cache de tokens expirados
     */
    public void cleanExpiredTokens() {
        tokenCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        logger.debug("Cleaned expired tokens from cache. Current cache size: {}", tokenCache.size());
    }
    
    /**
     * Limpia todo el cache
     */
    public void clearCache() {
        tokenCache.clear();
        logger.debug("Token cache cleared");
    }
    
    /**
     * Invalida un token agregándolo a la blacklist (logout)
     * @param token Token a invalidar
     */
    public void invalidateToken(String token) {
        if (token != null && !token.isEmpty()) {
            // Agregar a blacklist con timestamp
            tokenBlacklist.put(token, System.currentTimeMillis());
            // Remover del cache si existe
            tokenCache.remove(token);
            logger.info("Token invalidated (logout) for token: {}", token.substring(0, Math.min(10, token.length())) + "...");
        }
    }

    /**
     * Invalida todas las sesiones activas de un usuario (por email/subject del JWT).
     * Se usa cuando cambia rol/tenant/permisos.
     */
    public void invalidateSessionsForUser(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        userSessionInvalidAfter.put(email.toLowerCase(), System.currentTimeMillis());
        logger.info("All active sessions invalidated for user: {}", email);
    }

    /**
     * Verifica si un token fue emitido antes del corte de invalidez del usuario.
     */
    public boolean isTokenRevokedForUser(String email, long issuedAtMillis) {
        if (email == null || email.isBlank() || issuedAtMillis <= 0) {
            return false;
        }
        Long invalidAfter = userSessionInvalidAfter.get(email.toLowerCase());
        return invalidAfter != null && issuedAtMillis <= invalidAfter;
    }
    
    /**
     * Verifica si un token está en la blacklist (fue invalidado)
     * @param token Token a verificar
     * @return true si el token está invalidado, false en caso contrario
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return tokenBlacklist.containsKey(token);
    }
    
    /**
     * Limpia tokens expirados de la blacklist
     * Los tokens JWT expirados ya no son válidos, así que podemos limpiarlos de la blacklist
     */
    public void cleanExpiredBlacklistTokens() {
        // Limpiar tokens de la blacklist que tengan más de 24 horas (tiempo típico de expiración JWT)
        long maxAge = TimeUnit.HOURS.toMillis(24);
        tokenBlacklist.entrySet().removeIf(entry -> 
            System.currentTimeMillis() - entry.getValue() > maxAge
        );
        logger.debug("Cleaned expired tokens from blacklist. Current blacklist size: {}", tokenBlacklist.size());
    }
}

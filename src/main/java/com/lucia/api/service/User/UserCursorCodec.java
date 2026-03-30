package com.lucia.api.service.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucia.api.exception.BadRequestException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Cursor opaco estable para ORDER BY created_at DESC, id DESC (empate determinista).
 */
@Component
public class UserCursorCodec {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserCursorCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encode(OffsetDateTime createdAt, Long id) {
        if (createdAt == null || id == null) {
            return null;
        }
        try {
            CursorPayload p = new CursorPayload(createdAt.toString(), id);
            String json = objectMapper.writeValueAsString(p);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BadRequestException("No se pudo generar cursor de paginación.");
        }
    }

    public CursorPayload decode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException("Cursor vacío.");
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(raw.trim());
            CursorPayload p = objectMapper.readValue(bytes, CursorPayload.class);
            if (p.getCreatedAt() == null || p.getCreatedAt().isBlank() || p.getId() == null) {
                throw new BadRequestException("Cursor inválido.");
            }
            OffsetDateTime.parse(p.getCreatedAt());
            if (p.getId() < 1) {
                throw new BadRequestException("Cursor inválido.");
            }
            return p;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Cursor inválido o corrupto.");
        }
    }

    public OffsetDateTime toCreatedAt(CursorPayload p) {
        return OffsetDateTime.parse(p.getCreatedAt());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursorPayload {
        private String createdAt;
        private Long id;
    }
}

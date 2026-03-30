package com.lucia.api.service.Tenant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.model.dto.admin.TenantAdminCreateRequest;
import com.lucia.api.model.dto.admin.TenantAdminDTO;
import com.lucia.api.model.dto.admin.TenantAdminUpdateRequest;
import com.lucia.api.model.entity.Tenant;
import com.lucia.api.repository.Tenant.TenantRepository;
import com.lucia.api.repository.User.UserRepository;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantAdminService {

    /** Claves permitidas en {@code navigation_config} (roles + lista global de secciones ocultas). */
    private static final java.util.Set<String> NAV_CONFIG_KEYS =
            java.util.Set.of("admin", "user", "seller", "disabled");

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public TenantAdminService(
            TenantRepository tenantRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public List<TenantAdminDTO> listAll() {
        return tenantRepository.findAll().stream().map(this::toDto).toList();
    }

    public TenantAdminDTO getById(Long id) {
        Tenant t = tenantRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant no encontrado"));
        return toDto(t);
    }

    @Transactional
    public TenantAdminDTO create(TenantAdminCreateRequest req) {
        assertValidNavigationConfig(req.getNavigationConfig());
        OffsetDateTime now = OffsetDateTime.now();
        Tenant t = Tenant.builder()
                .name(req.getName().trim())
                .navigationConfig(normalizeNavJson(req.getNavigationConfig()))
                .createdAt(now)
                .updatedAt(now)
                .build();
        Tenant saved = tenantRepository.save(t);
        return toDto(saved);
    }

    @Transactional
    public TenantAdminDTO update(Long id, TenantAdminUpdateRequest req) {
        Tenant t = tenantRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant no encontrado"));
        if (req.getName() != null && !req.getName().isBlank()) {
            t.setName(req.getName().trim());
        }
        if (req.getNavigationConfig() != null) {
            assertValidNavigationConfig(req.getNavigationConfig());
            t.setNavigationConfig(normalizeNavJson(req.getNavigationConfig()));
        }
        t.setUpdatedAt(OffsetDateTime.now());
        return toDto(tenantRepository.save(t));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant no encontrado");
        }
        userRepository.clearTenantByTenantId(id);
        tenantRepository.deleteById(id);
    }

    private TenantAdminDTO toDto(Tenant t) {
        return TenantAdminDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .navigationConfig(t.getNavigationConfig())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    private String normalizeNavJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            Map<String, List<String>> map =
                    objectMapper.readValue(raw, new TypeReference<>() {});
            Map<String, List<String>> normalized = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> e : map.entrySet()) {
                normalized.put(e.getKey().toLowerCase(Locale.ROOT), e.getValue());
            }
            return objectMapper.writeValueAsString(normalized);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("navigation_config no es JSON válido");
        }
    }

    private void assertValidNavigationConfig(String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        Map<String, List<String>> map;
        try {
            map = objectMapper.readValue(raw, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "navigation_config debe ser un JSON objeto: "
                            + "{ \"admin\": [...], \"user\": [...], \"seller\": [...], \"disabled\": [...] }");
        }
        for (Map.Entry<String, List<String>> e : map.entrySet()) {
            String roleKey = e.getKey().toLowerCase(Locale.ROOT);
            if (!NAV_CONFIG_KEYS.contains(roleKey)) {
                throw new IllegalArgumentException(
                        "Clave no permitida: "
                                + e.getKey()
                                + ". Use: admin, user, seller, disabled.");
            }
            List<String> list = e.getValue();
            if (list == null) {
                throw new IllegalArgumentException("La lista del rol '" + roleKey + "' no puede ser null");
            }
            for (String s : list) {
                if (s == null || s.isBlank()) {
                    throw new IllegalArgumentException("Ids de sección vacíos no permitidos en rol " + roleKey);
                }
            }
        }
    }
}

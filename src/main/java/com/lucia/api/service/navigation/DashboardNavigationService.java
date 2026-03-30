package com.lucia.api.service.navigation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucia.api.model.dto.User.TenantBriefDTO;
import com.lucia.api.model.entity.Tenant;
import com.lucia.api.model.entity.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardNavigationService {

    private final ObjectMapper objectMapper;

    @Autowired
    public DashboardNavigationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Lee {@link Tenant#getNavigationConfig()}: JSON
     * {@code { "admin"|"user"|"seller": ["id", ...], "disabled": ["id", ...] }}.
     * Las entradas en {@code disabled} se excluyen del menú aunque figuren en el array del rol.
     * Sin configuración o sin lista para el rol: lista vacía (el front aplica menú por defecto).
     */
    public List<String> resolveNavigationKeys(User user) {
        if (user == null || user.getRole() == null) {
            return List.of();
        }
        Tenant tenant = user.getTenant();
        if (tenant == null
                || tenant.getNavigationConfig() == null
                || tenant.getNavigationConfig().isBlank()) {
            return List.of();
        }
        try {
            Map<String, List<String>> byRole =
                    objectMapper.readValue(tenant.getNavigationConfig(), new TypeReference<>() {});
            if (byRole == null || byRole.isEmpty()) {
                return List.of();
            }
            String roleKey = user.getRole().navigationConfigKey();
            List<String> configured = byRole.get(roleKey);
            if (configured == null || configured.isEmpty()) {
                return List.of();
            }
            List<String> forRole = dedupeTrimPreserveOrder(configured);
            List<String> disabledRaw = byRole.get("disabled");
            if (disabledRaw == null || disabledRaw.isEmpty()) {
                return forRole;
            }
            Set<String> disabled =
                    new HashSet<>(dedupeTrimPreserveOrder(disabledRaw));
            List<String> filtered = new ArrayList<>();
            for (String id : forRole) {
                if (!disabled.contains(id)) {
                    filtered.add(id);
                }
            }
            return filtered;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static List<String> dedupeTrimPreserveOrder(List<String> raw) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String s : raw) {
            if (s == null) {
                continue;
            }
            String t = s.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return new ArrayList<>(out);
    }

    public TenantBriefDTO toBrief(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        return TenantBriefDTO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .build();
    }
}

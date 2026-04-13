package com.lucia.api.model.dto.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Asignar o quitar tenant de un usuario (admin). {@code null} = sin organización. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTenantPatchDTO {

    @JsonProperty("tenant_id")
    private Long tenantId;

    /** Rol objetivo: admin, user o seller. Si viene, tenant se resuelve automáticamente por rol. */
    private String role;
}

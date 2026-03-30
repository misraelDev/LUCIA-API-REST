package com.lucia.api.model.dto.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fila de listado / detalle mínimo para el panel admin (contrato del dashboard). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSummaryDTO {

    private String id;

    private String email;

    /** Minúsculas: {@code user}, {@code admin}, {@code seller}. */
    private String role;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("email_confirmed")
    private boolean emailConfirmed;

    @JsonProperty("tenant_id")
    private Long tenantId;

    @JsonProperty("tenant_name")
    private String tenantName;
}

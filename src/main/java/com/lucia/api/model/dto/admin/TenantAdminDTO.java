package com.lucia.api.model.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantAdminDTO {

    private Long id;

    private String name;

    /** JSON: rol en minúsculas → lista de ids de sección. */
    @JsonProperty("navigation_config")
    private String navigationConfig;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
}

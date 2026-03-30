package com.lucia.api.model.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantAdminCreateRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @JsonProperty("navigation_config")
    private String navigationConfig;
}

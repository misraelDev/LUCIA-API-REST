package com.lucia.api.model.dto.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavigationResponseDTO {

    @JsonProperty("navigation")
    private List<String> navigation;

    @JsonProperty("tenant")
    private TenantBriefDTO tenant;
}

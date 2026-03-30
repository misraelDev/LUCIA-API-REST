package com.lucia.api.model.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Metadatos de paginación offset (misma forma que UserListMetaDTO, sin cursor). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageMetaDTO {

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("per_page")
    private int perPage;

    private long total;

    @JsonProperty("last_page")
    private int lastPage;
}

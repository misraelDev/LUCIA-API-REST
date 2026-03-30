package com.lucia.api.model.dto.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Metadatos de paginación (offset y/o cursor). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserListMetaDTO {

    /** En modo solo-cursor puede ser null. */
    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("per_page")
    private int perPage;

    private long total;

    /** En modo solo-cursor puede ser null (total sigue siendo por filtros). */
    @JsonProperty("last_page")
    private Integer lastPage;

    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("prev_cursor")
    private String prevCursor;
}

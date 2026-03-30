package com.lucia.api.model.dto.User;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDTO {

    private List<UserSummaryDTO> data;

    private UserListMetaDTO meta;
}

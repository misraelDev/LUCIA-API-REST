package com.lucia.api.model.dto.User;

import java.time.OffsetDateTime;
import com.lucia.api.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String paternalSurname;
    private String maternalSurname;
    private String phone;
    private String profileImageUrl;
    private User.Role role;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

package com.lucia.api.model.dto.Request;

import java.time.LocalDateTime;
import com.lucia.api.model.entity.Request.RequestStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String need;
    private String message;
    private String referralCode;
    private String sellerId;
    private RequestStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

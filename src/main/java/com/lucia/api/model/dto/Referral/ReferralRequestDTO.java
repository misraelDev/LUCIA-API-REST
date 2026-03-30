package com.lucia.api.model.dto.Referral;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralRequestDTO {
    @NotBlank(message = "El ID del vendedor es obligatorio")
    @Size(max = 100, message = "El ID del vendedor no puede exceder 100 caracteres")
    private String sellerId;

    @Size(max = 50, message = "El código de referido no puede exceder 50 caracteres")
    private String referralCode;

    private Boolean isActive;

    // Explicit getters and setters as fallback
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

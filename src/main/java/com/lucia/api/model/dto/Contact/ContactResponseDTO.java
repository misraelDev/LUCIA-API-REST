package com.lucia.api.model.dto.Contact;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static builder method
    public static ContactResponseDTOBuilder builder() {
        return new ContactResponseDTOBuilder();
    }

    public static class ContactResponseDTOBuilder {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ContactResponseDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ContactResponseDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ContactResponseDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ContactResponseDTOBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public ContactResponseDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ContactResponseDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ContactResponseDTO build() {
            ContactResponseDTO dto = new ContactResponseDTO();
            dto.id = this.id;
            dto.name = this.name;
            dto.email = this.email;
            dto.phoneNumber = this.phoneNumber;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            return dto;
        }
    }
}

package com.lucia.api.model.dto.User;


import com.lucia.api.model.entity.User;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data                  // Genera Getters, Setters, toString, equals y hashCode
@Builder               // Permite crear objetos con un patrón fluido (UserRequestDTO.builder()...)
@NoArgsConstructor     // IMPORTANTE: Necesario para que Jackson (Spring) pueda crear la instancia desde JSON
@AllArgsConstructor    // Genera un constructor con todos los argumentos
public class UserRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 254, message = "El email no puede exceder 254 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,128}$",
            message = "La contraseña debe incluir mayúscula, minúscula, un número y un carácter especial")
    private String password;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @Size(min = 2, max = 50, message = "El apellido paterno debe tener entre 2 y 50 caracteres")
    private String paternalSurname;

    @Size(max = 50, message = "El apellido materno no puede exceder 50 caracteres")
    private String maternalSurname;

    @Size(max = 32, message = "El teléfono no puede exceder 32 caracteres")
    private String phone;

    @Size(max = 500, message = "La URL de la imagen de perfil no puede exceder 500 caracteres")
    private String profileImageUrl;

    @NotNull(message = "El rol es obligatorio")
    private User.Role role;

    // Explicit getters and setters as fallback
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaternalSurname() {
        return paternalSurname;
    }

    public void setPaternalSurname(String paternalSurname) {
        this.paternalSurname = paternalSurname;
    }

    public String getMaternalSurname() {
        return maternalSurname;
    }

    public void setMaternalSurname(String maternalSurname) {
        this.maternalSurname = maternalSurname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    // Static builder method
    public static UserRequestDTOBuilder builder() {
        return new UserRequestDTOBuilder();
    }

    public static class UserRequestDTOBuilder {
        private String email;
        private String password;
        private String name;
        private String paternalSurname;
        private String maternalSurname;
        private String phone;
        private String profileImageUrl;
        private User.Role role;

        public UserRequestDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserRequestDTOBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserRequestDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserRequestDTOBuilder paternalSurname(String paternalSurname) {
            this.paternalSurname = paternalSurname;
            return this;
        }

        public UserRequestDTOBuilder maternalSurname(String maternalSurname) {
            this.maternalSurname = maternalSurname;
            return this;
        }

        public UserRequestDTOBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserRequestDTOBuilder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public UserRequestDTOBuilder role(User.Role role) {
            this.role = role;
            return this;
        }

        public UserRequestDTO build() {
            UserRequestDTO dto = new UserRequestDTO();
            dto.email = this.email;
            dto.password = this.password;
            dto.name = this.name;
            dto.paternalSurname = this.paternalSurname;
            dto.maternalSurname = this.maternalSurname;
            dto.phone = this.phone;
            dto.profileImageUrl = this.profileImageUrl;
            dto.role = this.role;
            return dto;
        }
    }
}
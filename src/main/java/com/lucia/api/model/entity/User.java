package com.lucia.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 254, message = "El email no puede exceder 254 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    /** Hash BCrypt u otro (hasta 255 caracteres en BD). */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "El hash de contraseña no puede exceder 255 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 50, message = "El apellido paterno no puede exceder 50 caracteres")
    @Column(name = "paternal_surname", length = 50)
    private String paternalSurname;

    @Size(max = 50, message = "El apellido materno no puede exceder 50 caracteres")
    @Column(name = "maternal_surname", length = 50)
    private String maternalSurname;

    @Size(max = 32, message = "El teléfono no puede exceder 32 caracteres")
    @Column(name = "phone", length = 32)
    private String phone;

    @NotNull(message = "El rol es obligatorio")
    @Convert(converter = UserRoleConverter.class)
    @Column(name = "role", nullable = false)
    private Role role;

    /** Cliente / tenant (opcional). Si es null, se usan menús por defecto del sistema. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Size(max = 500, message = "La URL de la imagen de perfil no puede exceder 500 caracteres")
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // Explicit getters as fallback
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Roles vigentes en API y BD. Cadenas legadas {@code BUYER}/{@code GUEST} en BD se mapean a {@link #USER}
     * vía {@link UserRoleConverter} y {@link #fromPersistedOrApiName(String)}.
     */
    public enum Role {
        SELLER,
        USER,
        ADMIN;

        /**
         * Acepta nombre de rol desde BD o API; {@code BUYER} y {@code GUEST} se normalizan a {@link #USER}.
         */
        public static Role fromPersistedOrApiName(String raw) {
            if (raw == null || raw.isBlank()) {
                return USER;
            }
            String u = raw.trim().toUpperCase(java.util.Locale.ROOT);
            return switch (u) {
                case "BUYER", "GUEST" -> USER;
                case "USER", "SELLER", "ADMIN" -> Role.valueOf(u);
                default -> throw new IllegalArgumentException("Rol no válido: " + raw);
            };
        }

        /**
         * Rol para JWT, Spring Security y claves de {@code navigation_config} ({@code admin}, {@code user}, {@code seller}).
         */
        public String apiAuthority() {
            return switch (this) {
                case USER -> "user";
                case SELLER -> "seller";
                case ADMIN -> "admin";
            };
        }

        public String navigationConfigKey() {
            return apiAuthority();
        }
    }

}

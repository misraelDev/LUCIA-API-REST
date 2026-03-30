package com.lucia.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cliente / organización (multi-tenant). {@code navigation_config} es JSON libre por rol; el API solo
 * devuelve la lista del rol actual sin validar ids (el front mapea ids conocidos a rutas).
 */
@Entity
@Table(name = "tenants", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * JSON: rol en minúsculas ({@code admin}, {@code user}, {@code seller}) → lista ordenada de ids
     * de sección (strings arbitrarios acordados con el cliente).
     */
    @Column(name = "navigation_config", columnDefinition = "TEXT")
    private String navigationConfig;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}

package com.lucia.api.repository.User;

import java.util.Optional;
import com.lucia.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tenant WHERE u.email = :email")
    Optional<User> findByEmailWithTenant(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tenant WHERE u.id = :id")
    Optional<User> findByIdWithTenant(@Param("id") Long id);

    /** Quita la organización a todos los usuarios asignados (antes de borrar el tenant). */
    @Modifying
    @Query("UPDATE User u SET u.tenant = null WHERE u.tenant.id = :tenantId")
    int clearTenantByTenantId(@Param("tenantId") Long tenantId);
}

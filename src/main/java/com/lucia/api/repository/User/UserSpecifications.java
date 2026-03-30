package com.lucia.api.repository.User;

import com.lucia.api.model.entity.User;
import jakarta.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {}

    /** Búsqueda insensible a mayúsculas en email, nombre y apellido paterno. */
    public static Specification<User> withFilters(String search, User.Role role) {
        return (root, query, cb) -> {
            List<Predicate> parts = new ArrayList<>();
            if (role != null) {
                parts.add(cb.equal(root.get("role"), role));
            }
            if (search != null && !search.isBlank()) {
                String q = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                List<Predicate> ors = new ArrayList<>();
                ors.add(cb.like(cb.lower(root.get("email")), q));
                ors.add(
                        cb.and(
                                cb.isNotNull(root.get("name")),
                                cb.like(cb.lower(root.get("name")), q)));
                ors.add(
                        cb.and(
                                cb.isNotNull(root.get("paternalSurname")),
                                cb.like(cb.lower(root.get("paternalSurname")), q)));
                parts.add(cb.or(ors.toArray(Predicate[]::new)));
            }
            if (parts.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(parts.toArray(Predicate[]::new));
        };
    }

    /** Filas estrictamente “posteriores” al ancla en orden DESC(created_at, id). */
    public static Specification<User> keysetAfter(OffsetDateTime createdAt, Long id) {
        return (root, query, cb) ->
                cb.or(
                        cb.lessThan(root.get("createdAt"), createdAt),
                        cb.and(
                                cb.equal(root.get("createdAt"), createdAt),
                                cb.lessThan(root.get("id"), id)));
    }

    /** Filas estrictamente “anteriores” al ancla (más nuevas) para paginar hacia atrás. */
    public static Specification<User> keysetBefore(OffsetDateTime createdAt, Long id) {
        return (root, query, cb) ->
                cb.or(
                        cb.greaterThan(root.get("createdAt"), createdAt),
                        cb.and(
                                cb.equal(root.get("createdAt"), createdAt),
                                cb.greaterThan(root.get("id"), id)));
    }

}

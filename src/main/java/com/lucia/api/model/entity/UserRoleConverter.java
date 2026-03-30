package com.lucia.api.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persiste solo {@link User.Role#USER}, {@link User.Role#SELLER}, {@link User.Role#ADMIN}.
 * Valores legados en BD {@code BUYER}/{@code GUEST} se leen como {@link User.Role#USER}.
 */
@Converter(autoApply = false)
public class UserRoleConverter implements AttributeConverter<User.Role, String> {

    @Override
    public String convertToDatabaseColumn(User.Role role) {
        return role == null ? null : role.name();
    }

    @Override
    public User.Role convertToEntityAttribute(String db) {
        return User.Role.fromPersistedOrApiName(db);
    }
}

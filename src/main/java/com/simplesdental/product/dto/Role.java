package com.simplesdental.product.dto;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum Role {
    ADMIN(EnumSet.allOf(Permission.class)),
    USER(EnumSet.of(
            Permission.VIEW_PRODUCT,
            Permission.VIEW_CATEGORY,
            Permission.UPDATE_PASSWORD
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        if(permission == null) {
            return false;
        }

        return permissions.contains(permission);
    }

    public static Role from(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return Role.USER;
    }


}

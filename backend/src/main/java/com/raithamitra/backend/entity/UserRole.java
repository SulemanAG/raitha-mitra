package com.raithamitra.backend.entity;

/**
 * User Role Enumeration defining platform permissions and domain roles.
 * Designed to support future roles (MACHINERY_OWNER, ADMIN) without breaking existing users.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public enum UserRole {
    FARMER,
    LABOURER,
    MACHINERY_OWNER,
    ADMIN
}

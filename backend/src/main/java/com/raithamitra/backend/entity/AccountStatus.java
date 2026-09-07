package com.raithamitra.backend.entity;

/**
 * Account Status Enumeration representing user account lifecycle states.
 * Replaces scattered boolean flags (isActive, isDeleted) with an explicit state machine.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public enum AccountStatus {
    ACTIVE,
    PENDING_VERIFICATION,
    SUSPENDED,
    DEACTIVATED
}

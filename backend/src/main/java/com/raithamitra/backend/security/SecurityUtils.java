package com.raithamitra.backend.security;

import com.raithamitra.backend.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utility class providing convenient static access to the currently authenticated SecurityUser details.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    public static SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser;
        }
        throw new UnauthorizedException("User is not authenticated");
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentUserMobile() {
        return getCurrentUser().getUsername();
    }
}

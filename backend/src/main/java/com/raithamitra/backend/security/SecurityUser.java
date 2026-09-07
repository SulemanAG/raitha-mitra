package com.raithamitra.backend.security;

import com.raithamitra.backend.entity.AccountStatus;
import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Security UserDetails implementation adapting UserEntity to Spring Security context.
 * Provides GrantedAuthorities derived from backend UserRole definitions (e.g. ROLE_FARMER, ROLE_LABOURER).
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
public class SecurityUser implements UserDetails {

    private final UUID id;
    private final String mobileNumber;
    private final String primaryRole;
    private final AccountStatus accountStatus;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(UserEntity user) {
        this.id = user.getId();
        this.mobileNumber = user.getMobileNumber();
        this.primaryRole = user.getPrimaryRole();
        this.accountStatus = user.getAccountStatus();
        this.authorities = mapRolesToAuthorities(user.getRoles());
    }

    public static SecurityUser fromEntity(UserEntity user) {
        return new SecurityUser(user);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getRole() {
        return primaryRole;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return ""; // OTP-based authentication has no stored password
    }

    @Override
    public String getUsername() {
        return mobileNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != AccountStatus.DEACTIVATED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }
}

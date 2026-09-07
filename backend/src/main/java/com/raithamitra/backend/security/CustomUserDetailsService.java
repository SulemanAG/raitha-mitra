package com.raithamitra.backend.security;

import com.raithamitra.backend.entity.UserEntity;
import com.raithamitra.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * Loads UserDetails by mobile number or User UUID.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mobileNumber));

        return SecurityUser.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return SecurityUser.fromEntity(user);
    }
}

package com.jobaresure.security;

import com.jobaresure.entity.User;
import com.jobaresure.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Loads users for Spring Security. The DAO authentication path (login by email +
 * password) uses {@link #loadUserByUsername(String)} with the email; the JWT
 * filter uses {@link #loadById(UUID)} since tokens carry the user id.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found for " + email));
        return new CustomUserDetails(user);
    }

    public UserDetails loadById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return new CustomUserDetails(user);
    }
}

package com.project.app.service;

import com.project.app.Role;
import com.project.app.config.ApiKeyUtil;
import com.project.app.entity.User;
import com.project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRole() == null || !user.getRole().name().equalsIgnoreCase(Role.ADMIN.name())) {
            user.setRole(Role.USER);
            userRepository.save(user);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    public ResponseEntity<Map<String, Object>> generateApiKey(String username) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            String apiKey = ApiKeyUtil.generateApiKey();
            user.setApiKey(apiKey);
            userRepository.save(user);

            response.put("status", "success");
            response.put("message", "API key generated successfully");
            response.put("apiKey", apiKey);
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> revokeApiKey(String username) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.setApiKey(null);
            userRepository.save(user);

            response.put("status", "success");
            response.put("message", "API key revoked successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    public User getUserByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid API key"));
    }
}
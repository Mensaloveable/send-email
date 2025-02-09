package com.project.app.service;

import com.project.app.enums.AuditAction;
import com.project.app.enums.Role;
import com.project.app.config.ApiKeyUtil;
import com.project.app.entity.User;
import com.project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuditLogService auditLogService;

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

    public ResponseEntity<Map<String, Object>> generateApiKey() {

        Map<String, Object> response = new HashMap<>();

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            log.info("Generating API key for user: {}", username);

            // Find the user by username
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String apiKey = ApiKeyUtil.generateApiKey();
            user.setApiKey(apiKey);
            userRepository.save(user);

            auditLogService.log(AuditAction.GENERATE_API_KEY.getAction(), "User", user.getId(), "API key generated");

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

    public ResponseEntity<Map<String, Object>> revokeApiKey() {

        Map<String, Object> response = new HashMap<>();

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            log.info("Revoking API key for user: {}", username);

            user.setApiKey(null);
            userRepository.save(user);

            auditLogService.log(AuditAction.REVOKE_API_KEY.getAction(), "User", user.getId(), "API key revoked");

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
package com.project.app.service;

import com.project.app.config.JwtUtil;
import com.project.app.dto.EmailRequest;
import com.project.app.entity.User;
import com.project.app.enums.AuditAction;
import com.project.app.enums.Role;
import com.project.app.exception.InvalidEmailException;
import com.project.app.exception.InvalidPasswordException;
import com.project.app.exception.UserAlreadyExistsException;
import com.project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.base.url}")
    private String baseUrl;

    public ResponseEntity<Map<String, Object>> login(User user) {

        Map<String, Object> response = new HashMap<>();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            User inUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if the user is enabled
            if (!inUser.isEnabled()) {
                response.put("status", "error");
                response.put("message", "Your account is not activated. Please check your email to activate your account.");
                return ResponseEntity.status(403).body(response); // Forbidden status
            }

            String jwt = jwtUtil.generateToken(userDetails);

            auditLogService.log(AuditAction.LOGIN.getAction(), "User", user.getId(), "User logged in successfully");

            response.put("status", "success");
            response.put("message", "User logged in successfully");
            response.put("token", jwt);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    public void logout() {

//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        auditLogService.log(AuditAction.LOGOUT.getAction(), "User", user.getId(), "User logged out successfully");

        jwtUtil.invalidateToken(username);
    }

    public ResponseEntity<Map<String, Object>> registerUser(User user) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Check if the user already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new UserAlreadyExistsException("User with username '" + user.getUsername() + "' already exists");
            }

            // Check if the email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
            }

            // Validate email using regex (optional, depending on whether you want to manually validate or rely on annotations)
            if (!user.getEmail().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                throw new InvalidEmailException("Invalid email format");
            }

            // Validate password strength
            if (!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                throw new InvalidPasswordException("Password must contain at least 8 characters, including at least one uppercase letter, one lowercase letter, one number, and one special character");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set the default role if not already set
            if (user.getRole() == null) {
                user.setRole(Role.USER);  // Set default role to USER if it's not specified
            }

            // Generate activation token
            String activationToken = UUID.randomUUID().toString();
            user.setToken(activationToken);

            // Save the user, but set enabled as false initially
            user.setIsEnabled(false);
            userRepository.save(user);

            // Send activation email
            EmailRequest emailRequest = EmailRequest.builder()
                    .to(user.getEmail())
                    .subject("Activate your account")
                    .from("no-reply@sendemailservice")
                    .body("Click the link below to activate your account: " + baseUrl + "/auth/activate-account/" + activationToken)
                    .isHtml(false)
                    .build();
            emailService.sendPlainEmail(emailRequest);

            auditLogService.log(AuditAction.REGISTER.getAction(), "User", user.getId(), "User registered successfully");

            response.put("status", "success");
            response.put("message", "User registered successfully");
            response.put("user", userRepository.save(user));
            return ResponseEntity.ok(response);

        } catch (UserAlreadyExistsException | InvalidEmailException | InvalidPasswordException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while registering the user");
            return ResponseEntity.status(500).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> activateAccount(String token) {

        Map<String, String> response = new HashMap<>();

        try {
            User user = userRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setToken(null);
            user.setIsEnabled(true);
            userRepository.save(user);

            auditLogService.log(AuditAction.ACTIVATE_ACCOUNT.getAction(), "User", user.getId(), "Account activated successfully");

            response.put("status", "success");
            response.put("message", "Account activated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while activating the account");
            return ResponseEntity.status(500).body(response);
        }
    }
}
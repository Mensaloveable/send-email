package com.project.app.controller;

import com.project.app.entity.User;
import com.project.app.service.AuthService;
import com.project.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String jwt = authService.login(username, password);
        return ResponseEntity.ok().body(jwt); // Return the JWT
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username) {
        authService.logout(username);
        return ResponseEntity.ok().body("Logged out successfully");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid User user) {
        User registeredUser = authService.registerUser(user);
        return ResponseEntity.ok().body(registeredUser);
    }

}

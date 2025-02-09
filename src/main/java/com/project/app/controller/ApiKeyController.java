package com.project.app.controller;

import com.project.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiKeyController {

    private final UserService userService;

    @PostMapping("/generate-api-key")
    public ResponseEntity<?> generateApiKey(@RequestParam String username) {
        String apiKey = userService.generateApiKey(username); // Generate an API key
        return ResponseEntity.ok().body(apiKey); // Return the API key
    }

    @PostMapping("/revoke-api-key")
    public ResponseEntity<?> revokeApiKey(@RequestParam String username) {
        userService.revokeApiKey(username); // Revoke the API key
        return ResponseEntity.ok().body("API key revoked successfully");
    }
}

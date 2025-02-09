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
        return userService.generateApiKey(username);
    }

    @PostMapping("/revoke-api-key")
    public ResponseEntity<?> revokeApiKey(@RequestParam String username) {
        return userService.revokeApiKey(username);
    }
}

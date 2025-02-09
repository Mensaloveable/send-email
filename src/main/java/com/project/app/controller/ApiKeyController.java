package com.project.app.controller;

import com.project.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiKeyController {

    private final UserService userService;

    @PostMapping("/generate-api-key")
    public ResponseEntity<?> generateApiKey() {
        return userService.generateApiKey();
    }

    @PostMapping("/revoke-api-key")
    public ResponseEntity<?> revokeApiKey() {
        return userService.revokeApiKey();
    }
}

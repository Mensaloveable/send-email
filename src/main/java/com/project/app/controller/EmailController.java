package com.project.app.controller;

import com.project.app.dto.EmailRequest;
import com.project.app.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody @Valid EmailRequest emailRequest, @RequestHeader("X-API-KEY") String apiKey) {
        try {
            // Validate required fields in EmailRequest
            if (emailRequest.getTo() == null || emailRequest.getSubject() == null || emailRequest.getBody() == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Missing required fields"));
            }

            // Process email request
            emailService.processEmailRequest(emailRequest, apiKey);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Email request processed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}

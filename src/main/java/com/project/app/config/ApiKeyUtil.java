package com.project.app.config;

import java.security.SecureRandom;
import java.util.Base64;

public class ApiKeyUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateApiKey() {
        byte[] randomBytes = new byte[24]; // 24 bytes = 192 bits
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes); // Base64 encode for URL safety
    }
}

package com.raj.adoptme.controller;

import com.raj.adoptme.dto.ApiResponse;
import com.raj.adoptme.dto.AuthRequest;
import com.raj.adoptme.dto.AuthResponse;
import com.raj.adoptme.dto.OtpVerificationRequest;
import com.raj.adoptme.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody AuthRequest request) {
        try {
            String message = authService.sendOtp(request);
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (Exception e) {
            logger.error("Error sending OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        try {
            AuthResponse authResponse = authService.verifyOtpAndLogin(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
        } catch (Exception e) {
            logger.error("Error verifying OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("Auth API is working!", "Test successful"));
    }
}

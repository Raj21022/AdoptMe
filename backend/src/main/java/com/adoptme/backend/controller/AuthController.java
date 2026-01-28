package com.adoptme.backend.controller;

import com.adoptme.backend.dto.*;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        ApiResponse response = new ApiResponse(
                true,
                "Registration successful! Please check your email for OTP verification.",
                authResponse
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        ApiResponse response = new ApiResponse(
                true,
                "Login successful!",
                authResponse
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyEmail(request.getEmail(), request.getOtp());
        ApiResponse response = new ApiResponse(true, "Email verified successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        ApiResponse response = new ApiResponse(true, "OTP sent successfully to your email!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.getUserByEmail(email);
        
        ApiResponse response = new ApiResponse(true, "Profile fetched successfully", user);
        return ResponseEntity.ok(response);
    }
}
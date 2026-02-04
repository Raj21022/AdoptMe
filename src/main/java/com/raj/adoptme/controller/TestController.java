package com.raj.adoptme.controller;

import com.raj.adoptme.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Public endpoint - no authentication required", null));
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<Map<String, String>>> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, String> data = new HashMap<>();
        data.put("email", authentication.getName());
        data.put("authorities", authentication.getAuthorities().toString());
        
        return ResponseEntity.ok(ApiResponse.success("Protected endpoint - you are authenticated!", data));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Admin endpoint - only admins can access", null));
    }

    @GetMapping("/lister")
    @PreAuthorize("hasRole('LISTER')")
    public ResponseEntity<ApiResponse<String>> listerEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Lister endpoint - only listers can access", null));
    }

    @GetMapping("/adopter")
    @PreAuthorize("hasRole('ADOPTER')")
    public ResponseEntity<ApiResponse<String>> adopterEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Adopter endpoint - only adopters can access", null));
    }
}

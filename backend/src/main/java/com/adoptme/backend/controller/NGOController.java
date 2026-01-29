package com.adoptme.backend.controller;

import com.adoptme.backend.dto.ApiResponse;
import com.adoptme.backend.dto.NGOProfileDto;
import com.adoptme.backend.dto.NGOProfileResponse;
import com.adoptme.backend.service.NGOService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ngo")
@CrossOrigin(origins = "*")
public class NGOController {

    @Autowired
    private NGOService ngoService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerNGO(
            @Valid @RequestBody NGOProfileDto dto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        NGOProfileResponse response = ngoService.createNGOProfile(dto, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "NGO registered successfully! Awaiting admin verification.", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getMyProfile(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        NGOProfileResponse response = ngoService.getMyNGOProfile(userId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Profile fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getNGOProfile(@PathVariable Long id) {
        NGOProfileResponse response = ngoService.getNGOProfileById(id);
        return ResponseEntity.ok(new ApiResponse(true, "NGO profile fetched successfully", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody NGOProfileDto dto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        NGOProfileResponse response = ngoService.updateNGOProfile(dto, userId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", response));
    }

    @GetMapping("/verified")
    public ResponseEntity<ApiResponse> getVerifiedNGOs() {
        List<NGOProfileResponse> ngos = ngoService.getAllVerifiedNGOs();
        
        Map<String, Object> data = new HashMap<>();
        data.put("ngos", ngos);
        data.put("count", ngos.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Verified NGOs fetched successfully", data));
    }

    // Admin endpoints
    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse> getPendingNGOs(Authentication authentication) {
        // TODO: Add admin role check
        List<NGOProfileResponse> ngos = ngoService.getAllPendingNGOs();
        
        Map<String, Object> data = new HashMap<>();
        data.put("ngos", ngos);
        data.put("count", ngos.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Pending NGOs fetched successfully", data));
    }

    @PutMapping("/admin/{id}/verify")
    public ResponseEntity<ApiResponse> verifyNGO(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        NGOProfileResponse response = ngoService.verifyNGO(id, adminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "NGO verified successfully", response));
    }

    @DeleteMapping("/admin/{id}/reject")
    public ResponseEntity<ApiResponse> rejectNGO(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        ngoService.rejectNGO(id, reason, adminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "NGO rejected successfully"));
    }

    @SuppressWarnings("unchecked")
    private Long getUserIdFromAuth(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return (Long) details.get("userId");
    }
}

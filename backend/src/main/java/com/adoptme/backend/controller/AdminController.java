package com.adoptme.backend.controller;

import com.adoptme.backend.dto.ApiResponse;
import com.adoptme.backend.entity.Animal;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.service.AdminService;
import com.adoptme.backend.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AnimalService animalService;

    @GetMapping("/animals/pending")
    public ResponseEntity<ApiResponse> getPendingAnimals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Animal> pendingAnimals = adminService.getPendingAnimals(page, size);
        
        Map<String, Object> data = new HashMap<>();
        data.put("animals", pendingAnimals.getContent());
        data.put("currentPage", pendingAnimals.getNumber());
        data.put("totalPages", pendingAnimals.getTotalPages());
        data.put("totalItems", pendingAnimals.getTotalElements());
        
        return ResponseEntity.ok(new ApiResponse(true, "Pending animals fetched successfully", data));
    }

    @PutMapping("/animals/{id}/approve")
    public ResponseEntity<ApiResponse> approveAnimal(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        Animal approvedAnimal = adminService.approveAnimal(id, adminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Animal approved successfully", approvedAnimal));
    }

    @PutMapping("/animals/{id}/reject")
    public ResponseEntity<ApiResponse> rejectAnimal(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Rejection reason is required"));
        }
        
        Long adminId = getUserIdFromAuth(authentication);
        Animal rejectedAnimal = adminService.rejectAnimal(id, reason, adminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Animal rejected successfully", rejectedAnimal));
    }

    @PutMapping("/users/{id}/make-admin")
    public ResponseEntity<ApiResponse> makeUserAdmin(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long requestingAdminId = getUserIdFromAuth(authentication);
        User updatedUser = adminService.makeUserAdmin(id, requestingAdminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "User is now an admin", updatedUser));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getStatistics() {
        Map<String, Long> stats = adminService.getStatistics();
        return ResponseEntity.ok(new ApiResponse(true, "Statistics fetched successfully", stats));
    }

    @SuppressWarnings("unchecked")
    private Long getUserIdFromAuth(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return (Long) details.get("userId");
    }
}
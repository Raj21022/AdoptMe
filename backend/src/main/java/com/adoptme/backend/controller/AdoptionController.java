package com.adoptme.backend.controller;

import com.adoptme.backend.dto.AdoptionRequestDto;
import com.adoptme.backend.dto.AdoptionRequestResponse;
import com.adoptme.backend.dto.ApiResponse;
import com.adoptme.backend.service.AdoptionService;
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
@RequestMapping("/api/adoptions")
@CrossOrigin(origins = "*")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse> createRequest(
            @Valid @RequestBody AdoptionRequestDto dto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        AdoptionRequestResponse response = adoptionService.createRequest(dto, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Adoption request sent successfully!", response));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse> getMyRequests(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<AdoptionRequestResponse> requests = adoptionService.getMyRequests(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("requests", requests);
        data.put("count", requests.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Requests fetched successfully", data));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse> getReceivedRequests(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<AdoptionRequestResponse> requests = adoptionService.getReceivedRequests(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("requests", requests);
        data.put("count", requests.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Received requests fetched successfully", data));
    }

    @PutMapping("/{id}/stage")
    public ResponseEntity<ApiResponse> updateStage(
            @PathVariable Long id,
            @RequestParam String stage,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        AdoptionRequestResponse response = adoptionService.updateStage(id, stage, userId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Stage updated successfully", response));
    }

    @PostMapping("/{id}/share-contact")
    public ResponseEntity<ApiResponse> shareContact(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        AdoptionRequestResponse response = adoptionService.shareContact(id, userId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Contact information shared", response));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelRequest(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        adoptionService.cancelRequest(id, userId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Request cancelled successfully"));
    }

    @SuppressWarnings("unchecked")
    private Long getUserIdFromAuth(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return (Long) details.get("userId");
    }
}
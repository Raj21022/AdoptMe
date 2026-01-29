package com.adoptme.backend.controller;

import com.adoptme.backend.dto.AnimalRequest;
import com.adoptme.backend.dto.AnimalResponse;
import com.adoptme.backend.dto.AnimalSearchRequest;
import com.adoptme.backend.dto.ApiResponse;
import com.adoptme.backend.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@CrossOrigin(origins = "*")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping
    public ResponseEntity<ApiResponse> createAnimal(
            @Valid @RequestBody AnimalRequest request,
            Authentication authentication) {
        
        // Extract userId from JWT token
        String email = authentication.getName();
        // We'll need to get userId from email - let's add a helper method
        
        AnimalResponse response = animalService.createAnimal(request, getUserIdFromAuth(authentication));
        
        String message = response.getApprovalStatus().equals("PENDING")
                ? "Animal posted successfully! It will be visible after admin approval."
                : "Animal posted successfully!";
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, message, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAnimal(@PathVariable Long id) {
        AnimalResponse response = animalService.getAnimalById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Animal fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAnimal(
            @PathVariable Long id,
            @Valid @RequestBody AnimalRequest request,
            Authentication authentication) {
        
        AnimalResponse response = animalService.updateAnimal(id, request, getUserIdFromAuth(authentication));
        return ResponseEntity.ok(new ApiResponse(true, "Animal updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAnimal(
            @PathVariable Long id,
            Authentication authentication) {
        
        animalService.deleteAnimal(id, getUserIdFromAuth(authentication));
        return ResponseEntity.ok(new ApiResponse(true, "Animal deleted successfully"));
    }

    @GetMapping("/my-listings")
    public ResponseEntity<ApiResponse> getMyListings(Authentication authentication) {
        List<AnimalResponse> listings = animalService.getMyListings(getUserIdFromAuth(authentication));
        
        Map<String, Object> data = new HashMap<>();
        data.put("listings", listings);
        data.put("count", listings.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Listings fetched successfully", data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchAnimals(
            @RequestParam(required = false) String animalType,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String animalSize,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        AnimalSearchRequest searchRequest = new AnimalSearchRequest();
        searchRequest.setAnimalType(animalType);
        searchRequest.setArea(area);
        searchRequest.setGender(gender);
        searchRequest.setAnimalSize(animalSize);
        searchRequest.setLatitude(latitude);
        searchRequest.setLongitude(longitude);
        searchRequest.setRadius(radius);
        searchRequest.setPage(page);
        searchRequest.setPageSize(pageSize);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);
        
        Page<AnimalResponse> animals = animalService.searchAnimals(searchRequest);
        
        Map<String, Object> data = new HashMap<>();
        data.put("animals", animals.getContent());
        data.put("currentPage", animals.getNumber());
        data.put("totalPages", animals.getTotalPages());
        data.put("totalItems", animals.getTotalElements());
        data.put("hasNext", animals.hasNext());
        data.put("hasPrevious", animals.hasPrevious());
        
        return ResponseEntity.ok(new ApiResponse(true, "Animals fetched successfully", data));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse> incrementView(@PathVariable Long id) {
        animalService.incrementViews(id);
        return ResponseEntity.ok(new ApiResponse(true, "View counted"));
    }

    // Helper method to extract userId from Authentication
    // Helper method to extract userId from Authentication
    @SuppressWarnings("unchecked")
    private Long getUserIdFromAuth(Authentication authentication) {
    Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
    return (Long) details.get("userId");
    }
    }
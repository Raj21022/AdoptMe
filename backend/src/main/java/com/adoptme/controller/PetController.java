package com.adoptme.controller;

import com.adoptme.dto.PetRequest;
import com.adoptme.dto.PetResponse;
import com.adoptme.security.UserPrincipal;
import com.adoptme.service.CloudinaryService;
import com.adoptme.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    
    @Autowired
    private PetService petService;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(file);
        return ResponseEntity.ok(imageUrl);
    }
    
    @PostMapping("/add")
    public ResponseEntity<PetResponse> addPet(
            @Valid @RequestBody PetRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PetResponse response = petService.addPet(request, currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type) {
        List<PetResponse> pets = petService.getAllPets(location, type);
        return ResponseEntity.ok(pets);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id) {
        PetResponse pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }
    
    @GetMapping("/my-pets")
    public ResponseEntity<List<PetResponse>> getMyPets(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<PetResponse> pets = petService.getMyPets(currentUser.getId());
        return ResponseEntity.ok(pets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PetResponse response = petService.updatePet(id, request, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String message = petService.deletePet(id, currentUser.getId());
        return ResponseEntity.ok(message);
    }
}

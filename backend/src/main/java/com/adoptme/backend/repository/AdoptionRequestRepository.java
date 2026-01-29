package com.adoptme.backend.repository;

import com.adoptme.backend.entity.AdoptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {
    
    // Find by requester
    List<AdoptionRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);
    
    // Find by animal
    List<AdoptionRequest> findByAnimalIdOrderByCreatedAtDesc(Long animalId);
    
    // Find by animal owner (to see all requests for my animals)
    @Query("SELECT ar FROM AdoptionRequest ar WHERE ar.animal.user.id = :ownerId ORDER BY ar.createdAt DESC")
    List<AdoptionRequest> findByAnimalOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    // Check if user already requested this animal
    Optional<AdoptionRequest> findByAnimalIdAndRequesterId(Long animalId, Long requesterId);
    
    // Count requests by stage
    Long countByStage(String stage);
}
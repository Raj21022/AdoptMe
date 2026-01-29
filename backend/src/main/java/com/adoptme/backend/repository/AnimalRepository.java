package com.adoptme.backend.repository;

import com.adoptme.backend.entity.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    
    // Find by user
    List<Animal> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find by status
    Page<Animal> findByStatusAndApprovalStatus(String status, String approvalStatus, Pageable pageable);
    
    // Find by approval status (for admin)
    Page<Animal> findByApprovalStatus(String approvalStatus, Pageable pageable);
    
    // Find by area and status
    Page<Animal> findByAreaAndStatusAndApprovalStatus(String area, String status, String approvalStatus, Pageable pageable);
    
    // Find by animal type and status
    Page<Animal> findByAnimalTypeAndStatusAndApprovalStatus(String animalType, String status, String approvalStatus, Pageable pageable);
    
    // Find by area, type and status
    Page<Animal> findByAreaAndAnimalTypeAndStatusAndApprovalStatus(
            String area, String animalType, String status, String approvalStatus, Pageable pageable);
    
    // Search by location (within radius) - custom query
    @Query("SELECT a FROM Animal a WHERE a.status = :status AND a.approvalStatus = :approvalStatus " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
           "sin(radians(a.latitude)))) <= :radius")
    Page<Animal> findByLocationWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("status") String status,
            @Param("approvalStatus") String approvalStatus,
            Pageable pageable);
    
    // Count by user
    Long countByUserId(Long userId);
    
    // Count by status
    Long countByStatus(String status);
    
    // Count by approval status
    Long countByApprovalStatus(String approvalStatus);
}
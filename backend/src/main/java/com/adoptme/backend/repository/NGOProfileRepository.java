package com.adoptme.backend.repository;

import com.adoptme.backend.entity.NGOProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NGOProfileRepository extends JpaRepository<NGOProfile, Long> {
    
    // Find by user ID
    Optional<NGOProfile> findByUserId(Long userId);
    
    // Find by NGO name
    Optional<NGOProfile> findByNgoName(String ngoName);
    
    // Find by registration number
    Optional<NGOProfile> findByRegistrationNumber(String registrationNumber);
    
    // Find all verified NGOs
    List<NGOProfile> findByVerifiedOrderByCreatedAtDesc(Boolean verified);
    
    // Find all pending NGOs (not verified)
    @Query("SELECT n FROM NGOProfile n WHERE n.verified = false ORDER BY n.createdAt DESC")
    List<NGOProfile> findAllPendingNGOs();
    
    // Count verified NGOs
    Long countByVerified(Boolean verified);
}
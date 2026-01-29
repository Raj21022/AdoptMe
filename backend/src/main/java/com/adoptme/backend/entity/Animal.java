package com.adoptme.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "animals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // User who posted the animal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Basic Information
    @Column(length = 100)
    private String name;
    
    @NotBlank(message = "Animal type is required")
    @Column(name = "animal_type", nullable = false, length = 50)
    private String animalType; // DOG, CAT, BIRD, RABBIT, etc.
    
    @Column(length = 100)
    private String breed;
    
    @Column(name = "age_years")
    private Integer ageYears;
    
    @Column(name = "age_months")
    private Integer ageMonths;
    
    @Column(length = 10)
    private String gender; // MALE, FEMALE, UNKNOWN
    
    @Column(length = 20)
    private String size; // SMALL, MEDIUM, LARGE
    
    @Column(length = 100)
    private String color;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Location Information
    @NotBlank(message = "Found location is required")
    @Column(name = "found_location", nullable = false, columnDefinition = "TEXT")
    private String foundLocation;
    
    @NotBlank(message = "Area is required")
    @Column(nullable = false, length = 100)
    private String area; // Kothrud, Aundh, etc.
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    // Shelter Information
    @NotBlank(message = "Shelter status is required")
    @Column(name = "shelter_status", nullable = false, length = 20)
    private String shelterStatus; // STREET, FOSTERED, SHELTER
    
    @Column(name = "shelter_address", columnDefinition = "TEXT")
    private String shelterAddress;
    
    @Column(name = "shelter_contact", length = 15)
    private String shelterContact;
    
    // Health Information
    @Column(nullable = false)
    private Boolean vaccinated = false;
    
    @Column(name = "vaccination_details", columnDefinition = "TEXT")
    private String vaccinationDetails;
    
    @Column(name = "health_issues", columnDefinition = "TEXT")
    private String healthIssues;
    
    // Behavior Information
    @Column(columnDefinition = "TEXT")
    private String temperament; // Friendly, Shy, Aggressive, etc.
    
    @Column(name = "good_with_kids")
    private Boolean goodWithKids;
    
    @Column(name = "good_with_pets")
    private Boolean goodWithPets;
    
    @Column(name = "potty_trained")
    private Boolean pottyTrained = false;
    
    @Column(name = "eating_habits", columnDefinition = "TEXT")
    private String eatingHabits;
    
    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;
    
    // Photos - JSON array of URLs
    @Column(nullable = false, columnDefinition = "TEXT")
    private String photos; // Store as JSON string: ["url1", "url2", "url3"]
    
    // Status
    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, ADOPTED, PENDING
    
    @Column(name = "approval_status", nullable = false, length = 20)
    private String approvalStatus = "PENDING"; // PENDING, APPROVED, REJECTED
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    // Metadata
    @Column(nullable = false)
    private Integer views = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
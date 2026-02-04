package com.raj.adoptme.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Species is required")
    @Column(nullable = false)
    private String species; // Dog, Cat, Bird, etc.

    private String breed;

    @NotNull(message = "Age is required")
    @Column(nullable = false)
    private Integer age; // in months

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "health_status")
    private String healthStatus; // Vaccinated, Neutered, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionStatus adoptionStatus = AdoptionStatus.AVAILABLE;

    @Column(name = "adoption_fee")
    private Double adoptionFee;

    @ElementCollection
    @CollectionTable(name = "animal_images", joinColumns = @JoinColumn(name = "animal_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lister_id", nullable = false)
    private User lister;

    @Column(name = "adopted_by_id")
    private Long adoptedById; // User ID who adopted

    @Column(name = "adopted_at")
    private LocalDateTime adoptedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Gender {
        MALE,
        FEMALE,
        UNKNOWN
    }

    public enum AdoptionStatus {
        AVAILABLE,
        PENDING,
        ADOPTED,
        UNAVAILABLE
    }
}

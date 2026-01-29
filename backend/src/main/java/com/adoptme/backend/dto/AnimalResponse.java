package com.adoptme.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnimalResponse {
    
    private Long id;
    
    // Owner info
    private Long userId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    
    // Basic Information
    private String name;
    private String animalType;
    private String breed;
    private Integer ageYears;
    private Integer ageMonths;
    private String gender;
    private String size;
    private String color;
    private String description;
    
    // Location
    private String foundLocation;
    private String area;
    private Double latitude;
    private Double longitude;
    
    // Shelter
    private String shelterStatus;
    private String shelterAddress;
    private String shelterContact;
    
    // Health
    private Boolean vaccinated;
    private String vaccinationDetails;
    private String healthIssues;
    
    // Behavior
    private String temperament;
    private Boolean goodWithKids;
    private Boolean goodWithPets;
    private Boolean pottyTrained;
    private String eatingHabits;
    private String specialNeeds;
    
    // Photos
    private List<String> photos;
    
    // Status
    private String status;
    private String approvalStatus;
    private String rejectionReason;
    
    // Metadata
    private Integer views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
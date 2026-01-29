package com.adoptme.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AnimalRequest {
    
    // Basic Information
    private String name;
    
    @NotBlank(message = "Animal type is required")
    private String animalType; // DOG, CAT, BIRD, etc.
    
    private String breed;
    private Integer ageYears;
    private Integer ageMonths;
    private String gender; // MALE, FEMALE, UNKNOWN
    private String size; // SMALL, MEDIUM, LARGE
    private String color;
    private String description;
    
    // Location
    @NotBlank(message = "Found location is required")
    private String foundLocation;
    
    @NotBlank(message = "Area is required")
    private String area;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    // Shelter
    @NotBlank(message = "Shelter status is required")
    private String shelterStatus; // STREET, FOSTERED, SHELTER
    
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
    
    // Photos - array of Cloudinary URLs
    @NotNull(message = "At least one photo is required")
    private List<String> photos;
}
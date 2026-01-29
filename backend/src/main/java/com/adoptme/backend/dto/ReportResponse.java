package com.adoptme.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    
    private Long id;
    
    // Animal info
    private Long animalId;
    private String animalName;
    private String animalType;
    private String animalArea;
    private String animalPhoto;
    private String animalStatus;
    
    // Reporter info
    private Long reporterId;
    private String reporterName;
    private String reporterEmail;
    
    // Animal owner info
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    
    // Report details
    private String reason;
    private String description;
    private String status;
    
    // Review info
    private Long reviewedBy;
    private String reviewerName;
    private LocalDateTime reviewedAt;
    private String actionTaken;
    
    private LocalDateTime createdAt;
}
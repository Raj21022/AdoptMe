package com.adoptme.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdoptionRequestResponse {
    
    private Long id;
    
    // Animal info
    private Long animalId;
    private String animalName;
    private String animalType;
    private String animalArea;
    private String animalPhoto;
    
    // Requester info
    private Long requesterId;
    private String requesterName;
    private String requesterEmail;
    private String requesterPhone;
    
    // Owner info
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    
    // Request details
    private String stage;
    private String requesterMessage;
    private Boolean contactShared;
    private String notes;
    private LocalDateTime meetingDate;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
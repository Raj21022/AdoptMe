package com.adoptme.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NGOProfileResponse {
    
    private Long id;
    
    // User info
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    
    // NGO info
    private String ngoName;
    private String registrationNumber;
    private String website;
    private String description;
    private String address;
    
    // Verification
    private List<String> verificationDocuments;
    private Boolean verified;
    private Long verifiedBy;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
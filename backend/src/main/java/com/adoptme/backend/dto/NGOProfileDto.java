package com.adoptme.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NGOProfileDto {
    
    @NotBlank(message = "NGO name is required")
    @Size(max = 200, message = "NGO name must be less than 200 characters")
    private String ngoName;
    
    private String registrationNumber;
    
    private String website;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private List<String> verificationDocuments; // URLs of uploaded documents
}
package com.adoptme.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportDto {
    
    @NotNull(message = "Animal ID is required")
    private Long animalId;
    
    @NotBlank(message = "Reason is required")
    private String reason; // FAKE, INAPPROPRIATE, ABUSE, SOLD, OTHER
    
    @NotBlank(message = "Description is required")
    private String description;
}
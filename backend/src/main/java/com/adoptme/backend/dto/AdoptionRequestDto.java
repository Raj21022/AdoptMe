package com.adoptme.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdoptionRequestDto {
    
    @NotNull(message = "Animal ID is required")
    private Long animalId;
    
    @NotBlank(message = "Please include a message about why you want to adopt")
    private String requesterMessage;
}
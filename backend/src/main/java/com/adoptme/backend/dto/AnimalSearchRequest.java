package com.adoptme.backend.dto;

import lombok.Data;

@Data
public class AnimalSearchRequest {
    
    // Filter options
    private String animalType; // Filter by type
    private String area; // Filter by area
    private String gender; // Filter by gender
    private String animalSize; // Filter by size (RENAMED from 'size')
    
    // Location-based search
    private Double latitude;
    private Double longitude;
    private Double radius; // in kilometers
    
    // Pagination
    private Integer page = 0;
    private Integer pageSize = 10; // RENAMED from 'size'
    
    // Sorting
    private String sortBy = "createdAt"; // createdAt, views
    private String sortDirection = "DESC"; // ASC, DESC
}
package com.adoptme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private String name;
    private Integer age;
    private String type;
    private String description;
    private String contactNumber;
    private String location;
    private String landmark;
    private String locationLink;
    private String vaccinationStatus;
    private Boolean stray;
    private String adoptionStatus;
    private List<String> imageUrls;
    private Long listedById;
    private String listedByName;
    private String listedByRole;
    private LocalDateTime createdAt;
}

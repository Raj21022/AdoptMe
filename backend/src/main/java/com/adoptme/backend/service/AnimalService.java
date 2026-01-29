package com.adoptme.backend.service;

import com.adoptme.backend.dto.AnimalRequest;
import com.adoptme.backend.dto.AnimalResponse;
import com.adoptme.backend.dto.AnimalSearchRequest;
import com.adoptme.backend.entity.Animal;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.exception.BadRequestException;
import com.adoptme.backend.exception.ResourceNotFoundException;
import com.adoptme.backend.repository.AnimalRepository;
import com.adoptme.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public AnimalResponse createAnimal(AnimalRequest request, Long userId) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate photos
        if (request.getPhotos() == null || request.getPhotos().isEmpty()) {
            throw new BadRequestException("At least one photo is required");
        }

        if (request.getPhotos().size() > 5) {
            throw new BadRequestException("Maximum 5 photos allowed");
        }

        // Create animal
        Animal animal = new Animal();
        animal.setUser(user);
        
        // Basic info
        animal.setName(request.getName());
        animal.setAnimalType(request.getAnimalType().toUpperCase());
        animal.setBreed(request.getBreed());
        animal.setAgeYears(request.getAgeYears());
        animal.setAgeMonths(request.getAgeMonths());
        animal.setGender(request.getGender() != null ? request.getGender().toUpperCase() : null);
        animal.setSize(request.getSize() != null ? request.getSize().toUpperCase() : null);
        animal.setColor(request.getColor());
        animal.setDescription(request.getDescription());
        
        // Location
        animal.setFoundLocation(request.getFoundLocation());
        animal.setArea(request.getArea());
        animal.setLatitude(request.getLatitude());
        animal.setLongitude(request.getLongitude());
        
        // Shelter
        animal.setShelterStatus(request.getShelterStatus().toUpperCase());
        animal.setShelterAddress(request.getShelterAddress());
        animal.setShelterContact(request.getShelterContact());
        
        // Health
        animal.setVaccinated(request.getVaccinated() != null ? request.getVaccinated() : false);
        animal.setVaccinationDetails(request.getVaccinationDetails());
        animal.setHealthIssues(request.getHealthIssues());
        
        // Behavior
        animal.setTemperament(request.getTemperament());
        animal.setGoodWithKids(request.getGoodWithKids());
        animal.setGoodWithPets(request.getGoodWithPets());
        animal.setPottyTrained(request.getPottyTrained() != null ? request.getPottyTrained() : false);
        animal.setEatingHabits(request.getEatingHabits());
        animal.setSpecialNeeds(request.getSpecialNeeds());
        
        // Photos - convert list to JSON string
        try {
            animal.setPhotos(objectMapper.writeValueAsString(request.getPhotos()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process photos");
        }
        
        // Status - pending approval for first-time posters
        Long userAnimalCount = animalRepository.countByUserId(userId);
        if (userAnimalCount == 0) {
            animal.setApprovalStatus("PENDING");
        } else {
            animal.setApprovalStatus("APPROVED"); // Auto-approve for repeat posters
        }
        animal.setStatus("AVAILABLE");
        animal.setViews(0);

        Animal savedAnimal = animalRepository.save(animal);
        
        return convertToResponse(savedAnimal);
    }

    public AnimalResponse getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        
        return convertToResponse(animal);
    }

    @Transactional
    public AnimalResponse updateAnimal(Long id, AnimalRequest request, Long userId) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        // Check ownership
        if (!animal.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only update your own listings");
        }

        // Update fields
        animal.setName(request.getName());
        animal.setAnimalType(request.getAnimalType().toUpperCase());
        animal.setBreed(request.getBreed());
        animal.setAgeYears(request.getAgeYears());
        animal.setAgeMonths(request.getAgeMonths());
        animal.setGender(request.getGender() != null ? request.getGender().toUpperCase() : null);
        animal.setSize(request.getSize() != null ? request.getSize().toUpperCase() : null);
        animal.setColor(request.getColor());
        animal.setDescription(request.getDescription());
        
        animal.setFoundLocation(request.getFoundLocation());
        animal.setArea(request.getArea());
        animal.setLatitude(request.getLatitude());
        animal.setLongitude(request.getLongitude());
        
        animal.setShelterStatus(request.getShelterStatus().toUpperCase());
        animal.setShelterAddress(request.getShelterAddress());
        animal.setShelterContact(request.getShelterContact());
        
        animal.setVaccinated(request.getVaccinated() != null ? request.getVaccinated() : false);
        animal.setVaccinationDetails(request.getVaccinationDetails());
        animal.setHealthIssues(request.getHealthIssues());
        
        animal.setTemperament(request.getTemperament());
        animal.setGoodWithKids(request.getGoodWithKids());
        animal.setGoodWithPets(request.getGoodWithPets());
        animal.setPottyTrained(request.getPottyTrained() != null ? request.getPottyTrained() : false);
        animal.setEatingHabits(request.getEatingHabits());
        animal.setSpecialNeeds(request.getSpecialNeeds());
        
        // Update photos if provided
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                animal.setPhotos(objectMapper.writeValueAsString(request.getPhotos()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process photos");
            }
        }

        Animal updatedAnimal = animalRepository.save(animal);
        return convertToResponse(updatedAnimal);
    }

    @Transactional
    public void deleteAnimal(Long id, Long userId) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        // Check ownership
        if (!animal.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own listings");
        }

        animalRepository.delete(animal);
    }

    public List<AnimalResponse> getMyListings(Long userId) {
        List<Animal> animals = animalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return animals.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<AnimalResponse> searchAnimals(AnimalSearchRequest searchRequest) {
        // Create pageable
        Sort sort = Sort.by(
                searchRequest.getSortDirection().equalsIgnoreCase("ASC") 
                        ? Sort.Direction.ASC 
                        : Sort.Direction.DESC,
                searchRequest.getSortBy()
        );
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize(), sort);

        Page<Animal> animals;

        // Location-based search
        if (searchRequest.getLatitude() != null && searchRequest.getLongitude() != null) {
            Double radius = searchRequest.getRadius() != null ? searchRequest.getRadius() : 10.0; // default 10km
            animals = animalRepository.findByLocationWithinRadius(
                    searchRequest.getLatitude(),
                    searchRequest.getLongitude(),
                    radius,
                    "AVAILABLE",
                    "APPROVED",
                    pageable
            );
        }
        // Filter by area and type
        else if (searchRequest.getArea() != null && searchRequest.getAnimalType() != null) {
            animals = animalRepository.findByAreaAndAnimalTypeAndStatusAndApprovalStatus(
                    searchRequest.getArea(),
                    searchRequest.getAnimalType().toUpperCase(),
                    "AVAILABLE",
                    "APPROVED",
                    pageable
            );
        }
        // Filter by area only
        else if (searchRequest.getArea() != null) {
            animals = animalRepository.findByAreaAndStatusAndApprovalStatus(
                    searchRequest.getArea(),
                    "AVAILABLE",
                    "APPROVED",
                    pageable
            );
        }
        // Filter by type only
        else if (searchRequest.getAnimalType() != null) {
            animals = animalRepository.findByAnimalTypeAndStatusAndApprovalStatus(
                    searchRequest.getAnimalType().toUpperCase(),
                    "AVAILABLE",
                    "APPROVED",
                    pageable
            );
        }
        // No filters - get all available and approved
        else {
            animals = animalRepository.findByStatusAndApprovalStatus(
                    "AVAILABLE",
                    "APPROVED",
                    pageable
            );
        }

        return animals.map(this::convertToResponse);
    }

    @Transactional
    public void incrementViews(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        animal.setViews(animal.getViews() + 1);
        animalRepository.save(animal);
    }

    // Helper method to convert Animal to AnimalResponse
    private AnimalResponse convertToResponse(Animal animal) {
        AnimalResponse response = new AnimalResponse();
        
        response.setId(animal.getId());
        
        // Owner info
        response.setUserId(animal.getUser().getId());
        response.setOwnerName(animal.getUser().getName());
        response.setOwnerPhone(animal.getUser().getPhone());
        response.setOwnerEmail(animal.getUser().getEmail());
        
        // Basic info
        response.setName(animal.getName());
        response.setAnimalType(animal.getAnimalType());
        response.setBreed(animal.getBreed());
        response.setAgeYears(animal.getAgeYears());
        response.setAgeMonths(animal.getAgeMonths());
        response.setGender(animal.getGender());
        response.setSize(animal.getSize());
        response.setColor(animal.getColor());
        response.setDescription(animal.getDescription());
        
        // Location
        response.setFoundLocation(animal.getFoundLocation());
        response.setArea(animal.getArea());
        response.setLatitude(animal.getLatitude());
        response.setLongitude(animal.getLongitude());
        
        // Shelter
        response.setShelterStatus(animal.getShelterStatus());
        response.setShelterAddress(animal.getShelterAddress());
        response.setShelterContact(animal.getShelterContact());
        
        // Health
        response.setVaccinated(animal.getVaccinated());
        response.setVaccinationDetails(animal.getVaccinationDetails());
        response.setHealthIssues(animal.getHealthIssues());
        
        // Behavior
        response.setTemperament(animal.getTemperament());
        response.setGoodWithKids(animal.getGoodWithKids());
        response.setGoodWithPets(animal.getGoodWithPets());
        response.setPottyTrained(animal.getPottyTrained());
        response.setEatingHabits(animal.getEatingHabits());
        response.setSpecialNeeds(animal.getSpecialNeeds());
        
        // Photos - convert JSON string to List
        try {
            List<String> photoList = objectMapper.readValue(
                    animal.getPhotos(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            response.setPhotos(photoList);
        } catch (JsonProcessingException e) {
            response.setPhotos(List.of());
        }
        
        // Status
        response.setStatus(animal.getStatus());
        response.setApprovalStatus(animal.getApprovalStatus());
        response.setRejectionReason(animal.getRejectionReason());
        
        // Metadata
        response.setViews(animal.getViews());
        response.setCreatedAt(animal.getCreatedAt());
        response.setUpdatedAt(animal.getUpdatedAt());
        
        return response;
    }
}
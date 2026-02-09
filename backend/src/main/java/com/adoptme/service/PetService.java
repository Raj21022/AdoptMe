package com.adoptme.service;

import com.adoptme.dto.PetRequest;
import com.adoptme.dto.PetResponse;
import com.adoptme.entity.Pet;
import com.adoptme.entity.User;
import com.adoptme.exception.CustomException;
import com.adoptme.repository.PetRepository;
import com.adoptme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    public PetResponse addPet(PetRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        if (user.getRole() != User.UserRole.COMMON_LISTER &&
            user.getRole() != User.UserRole.NGO_LISTER) {
            throw new CustomException("Only listers can add pets");
        }

        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setAge(request.getAge());
        pet.setType(request.getType());
        pet.setDescription(request.getDescription());
        pet.setContactNumber(request.getContactNumber());
        pet.setLocation(request.getLocation().trim());
        pet.setLandmark(normalizeOptional(request.getLandmark()));
        pet.setLocationLink(normalizeOptional(request.getLocationLink()));
        pet.setVaccinationStatus(normalizeOptional(request.getVaccinationStatus()));
        pet.setStray(request.getStray());
        Pet.AdoptionStatus adoptionStatus = parseAdoptionStatus(request.getAdoptionStatus());
        if (adoptionStatus != null) {
            pet.setAdoptionStatus(adoptionStatus);
        }
        pet.setImageUrls(sanitizeImageUrls(request.getImageUrls()));
        pet.setListedBy(user);

        Pet savedPet = petRepository.save(pet);

        return convertToResponse(savedPet);
    }

    public List<PetResponse> getAllPets(String location, String type) {
        List<Pet> pets;
        boolean hasLocation = location != null && !location.isBlank();
        boolean hasType = type != null && !type.isBlank();

        if (hasLocation && hasType) {
            pets = petRepository.findByLocationContainingIgnoreCaseAndTypeIgnoreCase(
                    location.trim(),
                    type.trim()
            );
        } else if (hasLocation) {
            pets = petRepository.findByLocationContainingIgnoreCase(location.trim());
        } else if (hasType) {
            pets = petRepository.findByTypeIgnoreCase(type.trim());
        } else {
            pets = petRepository.findAll();
        }

        return pets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new CustomException("Pet not found"));
        return convertToResponse(pet);
    }

    public List<PetResponse> getMyPets(Long userId) {
        return petRepository.findByListedById(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PetResponse updatePet(Long petId, PetRequest request, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new CustomException("Pet not found"));
        validatePetOwnership(pet, userId);

        pet.setName(request.getName());
        pet.setAge(request.getAge());
        pet.setType(request.getType());
        pet.setDescription(request.getDescription());
        pet.setContactNumber(request.getContactNumber());
        pet.setLocation(request.getLocation().trim());
        pet.setLandmark(normalizeOptional(request.getLandmark()));
        pet.setLocationLink(normalizeOptional(request.getLocationLink()));
        pet.setVaccinationStatus(normalizeOptional(request.getVaccinationStatus()));
        if (request.getStray() != null) {
            pet.setStray(request.getStray());
        }
        Pet.AdoptionStatus adoptionStatus = parseAdoptionStatus(request.getAdoptionStatus());
        if (adoptionStatus != null) {
            pet.setAdoptionStatus(adoptionStatus);
        }
        pet.setImageUrls(sanitizeImageUrls(request.getImageUrls()));

        Pet updatedPet = petRepository.save(pet);
        return convertToResponse(updatedPet);
    }

    public String deletePet(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new CustomException("Pet not found"));
        validatePetOwnership(pet, userId);

        petRepository.delete(pet);
        return "Pet deleted successfully";
    }

    private PetResponse convertToResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getAge(),
                pet.getType(),
                pet.getDescription(),
                pet.getContactNumber(),
                pet.getLocation(),
                pet.getLandmark(),
                pet.getLocationLink(),
                pet.getVaccinationStatus(),
                pet.getStray(),
                pet.getAdoptionStatus() != null ? pet.getAdoptionStatus().name() : null,
                pet.getImageUrls(),
                pet.getListedBy().getId(),
                pet.getListedBy().getName(),
                pet.getListedBy().getRole().name(),
                pet.getCreatedAt()
        );
    }

    private List<String> sanitizeImageUrls(List<String> imageUrls) {
        if (imageUrls == null) {
            return new ArrayList<>();
        }

        return imageUrls.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toList());
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Pet.AdoptionStatus parseAdoptionStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return Pet.AdoptionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CustomException("Invalid adoption status");
        }
    }

    private void validatePetOwnership(Pet pet, Long userId) {
        if (!Objects.equals(pet.getListedBy().getId(), userId)) {
            throw new CustomException("You can only manage pets added by you");
        }
    }
}

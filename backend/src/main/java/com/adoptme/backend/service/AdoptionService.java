package com.adoptme.backend.service;

import com.adoptme.backend.dto.AdoptionRequestDto;
import com.adoptme.backend.dto.AdoptionRequestResponse;
import com.adoptme.backend.entity.AdoptionRequest;
import com.adoptme.backend.entity.Animal;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.exception.BadRequestException;
import com.adoptme.backend.exception.ResourceNotFoundException;
import com.adoptme.backend.repository.AdoptionRequestRepository;
import com.adoptme.backend.repository.AnimalRepository;
import com.adoptme.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionService {

    @Autowired
    private AdoptionRequestRepository adoptionRequestRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public AdoptionRequestResponse createRequest(AdoptionRequestDto dto, Long userId) {
        // Get animal
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        // Check if animal is available
        if (!animal.getStatus().equals("AVAILABLE")) {
            throw new BadRequestException("This animal is not available for adoption");
        }

        if (!animal.getApprovalStatus().equals("APPROVED")) {
            throw new BadRequestException("This animal listing is not approved yet");
        }

        // Check if user is trying to adopt their own animal
        if (animal.getUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot adopt your own animal");
        }

        // Check if user already requested this animal
        Optional<AdoptionRequest> existing = adoptionRequestRepository
                .findByAnimalIdAndRequesterId(dto.getAnimalId(), userId);
        
        if (existing.isPresent()) {
            throw new BadRequestException("You have already requested to adopt this animal");
        }

        // Get requester
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create adoption request
        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setRequester(requester);
        request.setRequesterMessage(dto.getRequesterMessage());
        request.setStage("INTERESTED");
        request.setContactShared(false);

        AdoptionRequest savedRequest = adoptionRequestRepository.save(request);

        // Send notification to owner
        try {
            sendNewRequestNotification(animal.getUser(), animal, requester);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToResponse(savedRequest);
    }

    public List<AdoptionRequestResponse> getMyRequests(Long userId) {
        List<AdoptionRequest> requests = adoptionRequestRepository
                .findByRequesterIdOrderByCreatedAtDesc(userId);
        
        return requests.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AdoptionRequestResponse> getReceivedRequests(Long userId) {
        List<AdoptionRequest> requests = adoptionRequestRepository
                .findByAnimalOwnerIdOrderByCreatedAtDesc(userId);
        
        return requests.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdoptionRequestResponse updateStage(Long requestId, String stage, Long userId) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found"));

        // Only owner can update stage
        if (!request.getAnimal().getUser().getId().equals(userId)) {
            throw new BadRequestException("Only the animal owner can update the adoption stage");
        }

        // Validate stage
        List<String> validStages = List.of(
            "INTERESTED", "CONTACT_SHARED", "MEETING_SCHEDULED", 
            "HOME_VISIT_DONE", "ADOPTED", "CANCELLED"
        );
        
        if (!validStages.contains(stage)) {
            throw new BadRequestException("Invalid stage");
        }

        request.setStage(stage);

        // If adopted, mark animal as adopted
        if (stage.equals("ADOPTED")) {
            Animal animal = request.getAnimal();
            animal.setStatus("ADOPTED");
            animalRepository.save(animal);
        }

        AdoptionRequest updatedRequest = adoptionRequestRepository.save(request);
        
        // Notify requester
        try {
            sendStageUpdateNotification(request.getRequester(), request.getAnimal(), stage);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToResponse(updatedRequest);
    }

    @Transactional
    public AdoptionRequestResponse shareContact(Long requestId, Long userId) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found"));

        // Only owner can share contact
        if (!request.getAnimal().getUser().getId().equals(userId)) {
            throw new BadRequestException("Only the animal owner can share contact information");
        }

        request.setContactShared(true);
        if (request.getStage().equals("INTERESTED")) {
            request.setStage("CONTACT_SHARED");
        }

        AdoptionRequest updatedRequest = adoptionRequestRepository.save(request);
        return convertToResponse(updatedRequest);
    }

    @Transactional
    public void cancelRequest(Long requestId, Long userId) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found"));

        // Either requester or owner can cancel
        boolean isRequester = request.getRequester().getId().equals(userId);
        boolean isOwner = request.getAnimal().getUser().getId().equals(userId);

        if (!isRequester && !isOwner) {
            throw new BadRequestException("You cannot cancel this request");
        }

        request.setStage("CANCELLED");
        adoptionRequestRepository.save(request);
    }

    private AdoptionRequestResponse convertToResponse(AdoptionRequest request) {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        
        response.setId(request.getId());
        
        // Animal info
        Animal animal = request.getAnimal();
        response.setAnimalId(animal.getId());
        response.setAnimalName(animal.getName());
        response.setAnimalType(animal.getAnimalType());
        response.setAnimalArea(animal.getArea());
        
        // Get first photo
        try {
            List<String> photos = objectMapper.readValue(
                animal.getPhotos(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            response.setAnimalPhoto(photos.isEmpty() ? null : photos.get(0));
        } catch (JsonProcessingException e) {
            response.setAnimalPhoto(null);
        }
        
        // Requester info
        User requester = request.getRequester();
        response.setRequesterId(requester.getId());
        response.setRequesterName(requester.getName());
        response.setRequesterEmail(requester.getEmail());
        response.setRequesterPhone(requester.getPhone());
        
        // Owner info
        User owner = animal.getUser();
        response.setOwnerId(owner.getId());
        response.setOwnerName(owner.getName());
        response.setOwnerEmail(owner.getEmail());
        response.setOwnerPhone(owner.getPhone());
        
        // Request details
        response.setStage(request.getStage());
        response.setRequesterMessage(request.getRequesterMessage());
        response.setContactShared(request.getContactShared());
        response.setNotes(request.getNotes());
        response.setMeetingDate(request.getMeetingDate());
        
        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());
        
        return response;
    }

    private void sendNewRequestNotification(User owner, Animal animal, User requester) {
        String subject = "AdoptMe - New adoption request for " + animal.getName();
        String body = String.format(
            "Dear %s,\n\n" +
            "Great news! Someone is interested in adopting %s!\n\n" +
            "Requester: %s\n" +
            "Contact: %s\n\n" +
            "Please log in to your account to view the request and share your contact information.\n\n" +
            "Best regards,\n" +
            "AdoptMe Team",
            owner.getName(),
            animal.getName() != null ? animal.getName() : "your " + animal.getAnimalType(),
            requester.getName(),
            requester.getEmail()
        );
        
        emailService.sendEmail(owner.getEmail(), subject, body);
    }

    private void sendStageUpdateNotification(User requester, Animal animal, String stage) {
        String subject = "AdoptMe - Update on your adoption request";
        String statusMessage = switch(stage) {
            case "CONTACT_SHARED" -> "The owner has shared their contact information with you!";
            case "MEETING_SCHEDULED" -> "A meeting has been scheduled!";
            case "HOME_VISIT_DONE" -> "The home visit is complete!";
            case "ADOPTED" -> "Congratulations! The adoption is complete!";
            case "CANCELLED" -> "Unfortunately, this adoption request has been cancelled.";
            default -> "Your adoption request status has been updated to: " + stage;
        };
        
        String body = String.format(
            "Dear %s,\n\n" +
            "%s\n\n" +
            "Animal: %s\n\n" +
            "Please log in to your account for more details.\n\n" +
            "Best regards,\n" +
            "AdoptMe Team",
            requester.getName(),
            statusMessage,
            animal.getName() != null ? animal.getName() : "the " + animal.getAnimalType()
        );
        
        emailService.sendEmail(requester.getEmail(), subject, body);
    }
}
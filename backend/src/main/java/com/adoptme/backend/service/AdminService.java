package com.adoptme.backend.service;

import com.adoptme.backend.entity.Animal;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.exception.BadRequestException;
import com.adoptme.backend.exception.ResourceNotFoundException;
import com.adoptme.backend.repository.AnimalRepository;
import com.adoptme.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Page<Animal> getPendingAnimals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return animalRepository.findByApprovalStatus("PENDING", pageable);
    }

    @Transactional
    public Animal approveAnimal(Long animalId, Long adminId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!animal.getApprovalStatus().equals("PENDING")) {
            throw new BadRequestException("Animal is not pending approval");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        animal.setApprovalStatus("APPROVED");
        animal.setApprovedBy(admin);
        animal.setApprovedAt(LocalDateTime.now());

        Animal approvedAnimal = animalRepository.save(animal);

        // Send notification email to owner
        try {
            sendApprovalEmail(animal.getUser(), animal, true, null);
        } catch (Exception e) {
            // Log but don't fail the approval
            System.err.println("Failed to send approval email: " + e.getMessage());
        }

        return approvedAnimal;
    }

    @Transactional
    public Animal rejectAnimal(Long animalId, String reason, Long adminId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!animal.getApprovalStatus().equals("PENDING")) {
            throw new BadRequestException("Animal is not pending approval");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        animal.setApprovalStatus("REJECTED");
        animal.setRejectionReason(reason);
        animal.setApprovedBy(admin);
        animal.setApprovedAt(LocalDateTime.now());

        Animal rejectedAnimal = animalRepository.save(animal);

        // Send notification email to owner
        try {
            sendApprovalEmail(animal.getUser(), animal, false, reason);
        } catch (Exception e) {
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }

        return rejectedAnimal;
    }

    @Transactional
    public User makeUserAdmin(Long userId, Long requestingAdminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new BadRequestException("User is already an admin");
        }

        user.setRole(User.Role.ADMIN);
        return userRepository.save(user);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalAnimals", animalRepository.count());
        stats.put("availableAnimals", animalRepository.countByStatus("AVAILABLE"));
        stats.put("adoptedAnimals", animalRepository.countByStatus("ADOPTED"));
        stats.put("pendingApproval", animalRepository.countByApprovalStatus("PENDING"));
        stats.put("totalUsers", userRepository.count());
        
        return stats;
    }

    private void sendApprovalEmail(User owner, Animal animal, boolean approved, String reason) {
        String subject = approved 
                ? "AdoptMe - Your animal listing has been approved!" 
                : "AdoptMe - Your animal listing needs attention";
        
        String body;
        if (approved) {
            body = String.format(
                "Dear %s,\n\n" +
                "Great news! Your animal listing for '%s' has been approved and is now visible to potential adopters.\n\n" +
                "Animal Details:\n" +
                "Name: %s\n" +
                "Type: %s\n" +
                "Location: %s\n\n" +
                "You can manage your listing at any time from your account.\n\n" +
                "Thank you for helping animals find loving homes!\n\n" +
                "Best regards,\n" +
                "AdoptMe Team",
                owner.getName(),
                animal.getName() != null ? animal.getName() : "the " + animal.getAnimalType(),
                animal.getName() != null ? animal.getName() : "Not specified",
                animal.getAnimalType(),
                animal.getArea()
            );
        } else {
            body = String.format(
                "Dear %s,\n\n" +
                "We've reviewed your animal listing for '%s' and unfortunately it doesn't meet our guidelines at this time.\n\n" +
                "Reason: %s\n\n" +
                "Please review our listing guidelines and feel free to submit an updated listing.\n\n" +
                "If you have any questions, please contact us.\n\n" +
                "Best regards,\n" +
                "AdoptMe Team",
                owner.getName(),
                animal.getName() != null ? animal.getName() : "the " + animal.getAnimalType(),
                reason
            );
        }

        try {
            emailService.sendEmail(owner.getEmail(), subject, body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
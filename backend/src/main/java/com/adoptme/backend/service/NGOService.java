package com.adoptme.backend.service;

import com.adoptme.backend.dto.NGOProfileDto;
import com.adoptme.backend.dto.NGOProfileResponse;
import com.adoptme.backend.entity.NGOProfile;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.exception.BadRequestException;
import com.adoptme.backend.exception.ResourceNotFoundException;
import com.adoptme.backend.repository.NGOProfileRepository;
import com.adoptme.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NGOService {

    @Autowired
    private NGOProfileRepository ngoProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public NGOProfileResponse createNGOProfile(NGOProfileDto dto, Long userId) {
        // Check if user already has an NGO profile
        Optional<NGOProfile> existing = ngoProfileRepository.findByUserId(userId);
        if (existing.isPresent()) {
            throw new BadRequestException("You already have an NGO profile");
        }

        // Check if NGO name already exists
        Optional<NGOProfile> existingName = ngoProfileRepository.findByNgoName(dto.getNgoName());
        if (existingName.isPresent()) {
            throw new BadRequestException("An NGO with this name already exists");
        }

        // Check if registration number already exists (if provided)
        if (dto.getRegistrationNumber() != null && !dto.getRegistrationNumber().isEmpty()) {
            Optional<NGOProfile> existingReg = ngoProfileRepository
                    .findByRegistrationNumber(dto.getRegistrationNumber());
            if (existingReg.isPresent()) {
                throw new BadRequestException("This registration number is already registered");
            }
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Convert documents list to JSON
        String documentsJson;
        try {
            documentsJson = objectMapper.writeValueAsString(dto.getVerificationDocuments());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid documents format");
        }

        // Create NGO profile
        NGOProfile profile = new NGOProfile();
        profile.setUser(user);
        profile.setNgoName(dto.getNgoName());
        profile.setRegistrationNumber(dto.getRegistrationNumber());
        profile.setWebsite(dto.getWebsite());
        profile.setDescription(dto.getDescription());
        profile.setAddress(dto.getAddress());
        profile.setVerificationDocuments(documentsJson);
        profile.setVerified(false);

        NGOProfile savedProfile = ngoProfileRepository.save(profile);

        // Update user role to NGO
        user.setRole(User.Role.NGO);
        userRepository.save(user);

        // Notify admins about new NGO registration
        try {
            notifyAdminsAboutNewNGO(profile);
        } catch (Exception e) {
            System.err.println("Failed to notify admins: " + e.getMessage());
        }

        return convertToResponse(savedProfile);
    }

    public NGOProfileResponse getMyNGOProfile(Long userId) {
        NGOProfile profile = ngoProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
        
        return convertToResponse(profile);
    }

    public NGOProfileResponse getNGOProfileById(Long profileId) {
        NGOProfile profile = ngoProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
        
        return convertToResponse(profile);
    }

    @Transactional
    public NGOProfileResponse updateNGOProfile(NGOProfileDto dto, Long userId) {
        NGOProfile profile = ngoProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));

        // If already verified, don't allow changes to critical fields
        if (profile.getVerified()) {
            throw new BadRequestException("Cannot update verified NGO profile. Contact admin if changes needed.");
        }

        // Check if new NGO name conflicts with existing
        if (!profile.getNgoName().equals(dto.getNgoName())) {
            Optional<NGOProfile> existingName = ngoProfileRepository.findByNgoName(dto.getNgoName());
            if (existingName.isPresent()) {
                throw new BadRequestException("An NGO with this name already exists");
            }
        }

        // Convert documents list to JSON
        String documentsJson;
        try {
            documentsJson = objectMapper.writeValueAsString(dto.getVerificationDocuments());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid documents format");
        }

        // Update profile
        profile.setNgoName(dto.getNgoName());
        profile.setRegistrationNumber(dto.getRegistrationNumber());
        profile.setWebsite(dto.getWebsite());
        profile.setDescription(dto.getDescription());
        profile.setAddress(dto.getAddress());
        profile.setVerificationDocuments(documentsJson);

        NGOProfile updatedProfile = ngoProfileRepository.save(profile);
        return convertToResponse(updatedProfile);
    }

    public List<NGOProfileResponse> getAllVerifiedNGOs() {
        List<NGOProfile> profiles = ngoProfileRepository.findByVerifiedOrderByCreatedAtDesc(true);
        return profiles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<NGOProfileResponse> getAllPendingNGOs() {
        List<NGOProfile> profiles = ngoProfileRepository.findAllPendingNGOs();
        return profiles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NGOProfileResponse verifyNGO(Long profileId, Long adminId) {
        NGOProfile profile = ngoProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRole().equals(User.Role.ADMIN)) {
            throw new BadRequestException("Only admins can verify NGOs");
        }

        if (profile.getVerified()) {
            throw new BadRequestException("NGO is already verified");
        }

        profile.setVerified(true);
        profile.setVerifiedBy(admin);
        profile.setVerifiedAt(LocalDateTime.now());

        NGOProfile verifiedProfile = ngoProfileRepository.save(profile);

        // Notify NGO about verification
        try {
            notifyNGOAboutVerification(profile.getUser(), profile.getNgoName());
        } catch (Exception e) {
            System.err.println("Failed to notify NGO: " + e.getMessage());
        }

        return convertToResponse(verifiedProfile);
    }

    @Transactional
    public void rejectNGO(Long profileId, String reason, Long adminId) {
        NGOProfile profile = ngoProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRole().equals(User.Role.ADMIN)) {
            throw new BadRequestException("Only admins can reject NGOs");
        }

        // Notify NGO about rejection
        try {
            notifyNGOAboutRejection(profile.getUser(), profile.getNgoName(), reason);
        } catch (Exception e) {
            System.err.println("Failed to notify NGO: " + e.getMessage());
        }

        // Delete the profile (or you could mark it as rejected instead)
        ngoProfileRepository.delete(profile);

        // Revert user role back to USER
        User user = profile.getUser();
        user.setRole(User.Role.USER);
        userRepository.save(user);
    }

    private NGOProfileResponse convertToResponse(NGOProfile profile) {
        NGOProfileResponse response = new NGOProfileResponse();
        
        response.setId(profile.getId());
        
        // User info
        User user = profile.getUser();
        response.setUserId(user.getId());
        response.setUserName(user.getName());
        response.setUserEmail(user.getEmail());
        response.setUserPhone(user.getPhone());
        
        // NGO info
        response.setNgoName(profile.getNgoName());
        response.setRegistrationNumber(profile.getRegistrationNumber());
        response.setWebsite(profile.getWebsite());
        response.setDescription(profile.getDescription());
        response.setAddress(profile.getAddress());
        
        // Parse documents JSON
        try {
            List<String> documents = objectMapper.readValue(
                profile.getVerificationDocuments(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            response.setVerificationDocuments(documents);
        } catch (JsonProcessingException e) {
            response.setVerificationDocuments(List.of());
        }
        
        // Verification info
        response.setVerified(profile.getVerified());
        if (profile.getVerifiedBy() != null) {
            response.setVerifiedBy(profile.getVerifiedBy().getId());
            response.setVerifiedByName(profile.getVerifiedBy().getName());
        }
        response.setVerifiedAt(profile.getVerifiedAt());
        
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        
        return response;
    }

    private void notifyAdminsAboutNewNGO(NGOProfile profile) {
        String subject = "AdoptMe - New NGO Registration";
        String body = String.format(
            "A new NGO has registered and needs verification.\n\n" +
            "NGO Name: %s\n" +
            "Registration Number: %s\n" +
            "Contact: %s (%s)\n\n" +
            "Please log in to the admin panel to review and verify.\n\n" +
            "AdoptMe Team",
            profile.getNgoName(),
            profile.getRegistrationNumber() != null ? profile.getRegistrationNumber() : "Not provided",
            profile.getUser().getName(),
            profile.getUser().getEmail()
        );
        
        // TODO: Get admin emails from database
        // emailService.sendEmail("admin@adoptme.com", subject, body);
    }

    private void notifyNGOAboutVerification(User user, String ngoName) {
        String subject = "AdoptMe - Your NGO is Verified! 🎉";
        String body = String.format(
            "Dear %s,\n\n" +
            "Congratulations! Your NGO '%s' has been verified.\n\n" +
            "You now have a verified badge on your profile and listings.\n" +
            "This helps build trust with potential adopters.\n\n" +
            "Thank you for being part of AdoptMe!\n\n" +
            "Best regards,\n" +
            "AdoptMe Team",
            user.getName(),
            ngoName
        );
        
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    private void notifyNGOAboutRejection(User user, String ngoName, String reason) {
        String subject = "AdoptMe - NGO Verification Status";
        String body = String.format(
            "Dear %s,\n\n" +
            "Thank you for registering '%s' on AdoptMe.\n\n" +
            "Unfortunately, we couldn't verify your NGO at this time.\n" +
            "Reason: %s\n\n" +
            "If you believe this is an error or would like to provide additional information, " +
            "please contact us at support@adoptme.com.\n\n" +
            "Best regards,\n" +
            "AdoptMe Team",
            user.getName(),
            ngoName,
            reason
        );
        
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
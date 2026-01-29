package com.adoptme.backend.service;

import com.adoptme.backend.dto.ReportDto;
import com.adoptme.backend.dto.ReportResponse;
import com.adoptme.backend.entity.Animal;
import com.adoptme.backend.entity.Report;
import com.adoptme.backend.entity.User;
import com.adoptme.backend.exception.BadRequestException;
import com.adoptme.backend.exception.ResourceNotFoundException;
import com.adoptme.backend.repository.AnimalRepository;
import com.adoptme.backend.repository.ReportRepository;
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
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public ReportResponse createReport(ReportDto dto, Long userId) {
        // Get animal
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        // Check if user is trying to report their own animal
        if (animal.getUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot report your own animal listing");
        }

        // Check if user already reported this animal
        Optional<Report> existingReport = reportRepository
                .findByAnimalIdAndReporterId(dto.getAnimalId(), userId);
        
        if (existingReport.isPresent()) {
            throw new BadRequestException("You have already reported this animal");
        }

        // Validate reason
        List<String> validReasons = List.of("FAKE", "INAPPROPRIATE", "ABUSE", "SOLD", "OTHER");
        if (!validReasons.contains(dto.getReason())) {
            throw new BadRequestException("Invalid report reason");
        }

        // Get reporter
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create report
        Report report = new Report();
        report.setAnimal(animal);
        report.setReporter(reporter);
        report.setReason(dto.getReason());
        report.setDescription(dto.getDescription());
        report.setStatus("PENDING");

        Report savedReport = reportRepository.save(report);

        // Notify admins
        try {
            notifyAdminsAboutReport(animal, reporter, dto.getReason());
        } catch (Exception e) {
            System.err.println("Failed to notify admins: " + e.getMessage());
        }

        // If multiple reports, automatically flag the listing
        Long reportCount = reportRepository.countByAnimalId(animal.getId());
        if (reportCount >= 3 && !animal.getStatus().equals("FLAGGED")) {
            animal.setStatus("FLAGGED");
            animalRepository.save(animal);
        }

        return convertToResponse(savedReport);
    }

    public List<ReportResponse> getMyReports(Long userId) {
        List<Report> reports = reportRepository.findByReporterIdOrderByCreatedAtDesc(userId);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ReportResponse> getAllPendingReports() {
        List<Report> reports = reportRepository.findAllPendingReports();
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ReportResponse> getReportsByStatus(String status) {
        List<Report> reports = reportRepository.findByStatusOrderByCreatedAtDesc(status);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ReportResponse> getReportsForAnimal(Long animalId) {
        List<Report> reports = reportRepository.findByAnimalIdOrderByCreatedAtDesc(animalId);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse reviewReport(Long reportId, String action, Long adminId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRole().equals("ADMIN")) {
            throw new BadRequestException("Only admins can review reports");
        }

        report.setStatus("REVIEWED");
        report.setReviewedBy(admin);
        report.setReviewedAt(LocalDateTime.now());
        report.setActionTaken(action);

        Report updatedReport = reportRepository.save(report);

        // Notify reporter about the review
        try {
            notifyReporterAboutReview(report.getReporter(), report.getAnimal(), action);
        } catch (Exception e) {
            System.err.println("Failed to notify reporter: " + e.getMessage());
        }

        return convertToResponse(updatedReport);
    }

    @Transactional
    public void takeAction(Long reportId, String action, Long adminId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRole().equals("ADMIN")) {
            throw new BadRequestException("Only admins can take action on reports");
        }

        Animal animal = report.getAnimal();

        // Take action based on the action type
        switch (action) {
            case "REMOVE_LISTING":
                animal.setStatus("REMOVED");
                animal.setApprovalStatus("REJECTED");
                animal.setRejectionReason("Removed due to report: " + report.getReason());
                animalRepository.save(animal);
                break;
            case "WARNING":
                // Just mark as reviewed with warning
                break;
            case "NO_ACTION":
                // Dismiss the report
                break;
            default:
                throw new BadRequestException("Invalid action type");
        }

        report.setStatus("ACTION_TAKEN");
        report.setActionTaken(action);
        report.setReviewedBy(admin);
        report.setReviewedAt(LocalDateTime.now());
        reportRepository.save(report);

        // Notify animal owner about action taken
        try {
            notifyOwnerAboutAction(animal.getUser(), animal, action);
        } catch (Exception e) {
            System.err.println("Failed to notify owner: " + e.getMessage());
        }
    }

    private ReportResponse convertToResponse(Report report) {
        ReportResponse response = new ReportResponse();
        
        response.setId(report.getId());
        
        // Animal info
        Animal animal = report.getAnimal();
        response.setAnimalId(animal.getId());
        response.setAnimalName(animal.getName());
        response.setAnimalType(animal.getAnimalType());
        response.setAnimalArea(animal.getArea());
        response.setAnimalStatus(animal.getStatus());
        
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
        
        // Reporter info (might be null if user deleted account)
        if (report.getReporter() != null) {
            User reporter = report.getReporter();
            response.setReporterId(reporter.getId());
            response.setReporterName(reporter.getName());
            response.setReporterEmail(reporter.getEmail());
        }
        
        // Animal owner info
        User owner = animal.getUser();
        response.setOwnerId(owner.getId());
        response.setOwnerName(owner.getName());
        response.setOwnerEmail(owner.getEmail());
        
        // Report details
        response.setReason(report.getReason());
        response.setDescription(report.getDescription());
        response.setStatus(report.getStatus());
        
        // Review info
        if (report.getReviewedBy() != null) {
            response.setReviewedBy(report.getReviewedBy().getId());
            response.setReviewerName(report.getReviewedBy().getName());
        }
        response.setReviewedAt(report.getReviewedAt());
        response.setActionTaken(report.getActionTaken());
        
        response.setCreatedAt(report.getCreatedAt());
        
        return response;
    }

    private void notifyAdminsAboutReport(Animal animal, User reporter, String reason) {
        String subject = "AdoptMe - New Report Received";
        String body = String.format(
            "A new report has been submitted.\n\n" +
            "Animal: %s (%s)\n" +
            "Area: %s\n" +
            "Reported by: %s (%s)\n" +
            "Reason: %s\n\n" +
            "Please log in to the admin panel to review this report.\n\n" +
            "AdoptMe Team",
            animal.getName() != null ? animal.getName() : "Unnamed",
            animal.getAnimalType(),
            animal.getArea(),
            reporter.getName(),
            reporter.getEmail(),
            reason
        );
        
        // TODO: Get admin emails from database
        // For now, you can hardcode your admin email for testing
        // emailService.sendEmail("admin@adoptme.com", subject, body);
    }

    private void notifyReporterAboutReview(User reporter, Animal animal, String action) {
        String subject = "AdoptMe - Your Report Has Been Reviewed";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your report for the %s in %s has been reviewed.\n\n" +
            "Action taken: %s\n\n" +
            "Thank you for helping keep AdoptMe safe!\n\n" +
            "Best regards,\n" +
            "AdoptMe Team",
            reporter.getName(),
            animal.getAnimalType(),
            animal.getArea(),
            action
        );
        
        emailService.sendEmail(reporter.getEmail(), subject, body);
    }

    private void notifyOwnerAboutAction(User owner, Animal animal, String action) {
        if (action.equals("NO_ACTION")) {
            return; // Don't notify owner if no action was taken
        }
        
        String subject = "AdoptMe - Action Taken on Your Listing";
        String body = String.format(
            "Dear %s,\n\n" +
            "We've received a report about your animal listing:\n" +
            "Animal: %s (%s)\n" +
            "Area: %s\n\n" +
            "Action taken: %s\n\n" +
            "If you believe this was a mistake, please contact us at support@adoptme.com.\n\n" +
            "Best regards,\n" +
            "AdoptMe Team",
            owner.getName(),
            animal.getName() != null ? animal.getName() : "Unnamed",
            animal.getAnimalType(),
            animal.getArea(),
            action
        );
        
        emailService.sendEmail(owner.getEmail(), subject, body);
    }
}

package com.adoptme.backend.controller;

import com.adoptme.backend.dto.ApiResponse;
import com.adoptme.backend.dto.ReportDto;
import com.adoptme.backend.dto.ReportResponse;
import com.adoptme.backend.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse> createReport(
            @Valid @RequestBody ReportDto dto,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuth(authentication);
        ReportResponse response = reportService.createReport(dto, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Report submitted successfully. We'll review it soon.", response));
    }

    @GetMapping("/my-reports")
    public ResponseEntity<ApiResponse> getMyReports(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<ReportResponse> reports = reportService.getMyReports(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("reports", reports);
        data.put("count", reports.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Reports fetched successfully", data));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<ApiResponse> getReportsForAnimal(@PathVariable Long animalId) {
        List<ReportResponse> reports = reportService.getReportsForAnimal(animalId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("reports", reports);
        data.put("count", reports.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Reports fetched successfully", data));
    }

    // Admin endpoints
    @GetMapping("/admin/pending")
    public ResponseEntity<ApiResponse> getPendingReports(Authentication authentication) {
        // TODO: Add admin role check
        List<ReportResponse> reports = reportService.getAllPendingReports();
        
        Map<String, Object> data = new HashMap<>();
        data.put("reports", reports);
        data.put("count", reports.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Pending reports fetched successfully", data));
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<ApiResponse> getReportsByStatus(
            @PathVariable String status,
            Authentication authentication) {
        
        // TODO: Add admin role check
        List<ReportResponse> reports = reportService.getReportsByStatus(status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("reports", reports);
        data.put("count", reports.size());
        
        return ResponseEntity.ok(new ApiResponse(true, "Reports fetched successfully", data));
    }

    @PutMapping("/admin/{id}/review")
    public ResponseEntity<ApiResponse> reviewReport(
            @PathVariable Long id,
            @RequestParam String action,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        ReportResponse response = reportService.reviewReport(id, action, adminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Report reviewed successfully", response));
    }

    @PutMapping("/admin/{id}/take-action")
    public ResponseEntity<ApiResponse> takeAction(
            @PathVariable Long id,
            @RequestParam String action,
            Authentication authentication) {
        
        Long adminId = getUserIdFromAuth(authentication);
        reportService.takeAction(id, action, adminId);
        
        return ResponseEntity.ok(new ApiResponse(true, "Action taken successfully"));
    }

    @SuppressWarnings("unchecked")
    private Long getUserIdFromAuth(Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return (Long) details.get("userId");
    }
}
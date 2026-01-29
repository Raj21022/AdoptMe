package com.adoptme.backend.repository;

import com.adoptme.backend.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Find all reports for a specific animal
    List<Report> findByAnimalIdOrderByCreatedAtDesc(Long animalId);
    
    // Find reports by status
    List<Report> findByStatusOrderByCreatedAtDesc(String status);
    
    // Find reports by reporter
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
    
    // Check if user already reported this animal
    Optional<Report> findByAnimalIdAndReporterId(Long animalId, Long reporterId);
    
    // Count reports by status
    Long countByStatus(String status);
    
    // Count reports for a specific animal
    Long countByAnimalId(Long animalId);
    
    // Get all pending reports (for admin)
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' ORDER BY r.createdAt DESC")
    List<Report> findAllPendingReports();
}
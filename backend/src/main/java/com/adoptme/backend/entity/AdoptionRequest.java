package com.adoptme.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoption_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    
    @Column(nullable = false, length = 30)
    private String stage = "INTERESTED"; 
    // INTERESTED, CONTACT_SHARED, MEETING_SCHEDULED, HOME_VISIT_DONE, ADOPTED, CANCELLED
    
    @Column(name = "requester_message", columnDefinition = "TEXT")
    private String requesterMessage;
    
    @Column(name = "contact_shared", nullable = false)
    private Boolean contactShared = false;
    
    @Column(columnDefinition = "TEXT")
    private String notes; // Internal notes for tracking
    
    @Column(name = "meeting_date")
    private LocalDateTime meetingDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
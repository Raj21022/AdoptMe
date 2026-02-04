package com.raj.adoptme.repository;

import com.raj.adoptme.entity.Otp;
import com.raj.adoptme.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    
    Optional<Otp> findByUserAndOtpCodeAndIsUsedFalseAndExpiresAtAfter(
        User user, 
        String otpCode, 
        LocalDateTime currentTime
    );
    
    Optional<Otp> findTopByUserOrderByCreatedAtDesc(User user);
    
    void deleteByExpiresAtBefore(LocalDateTime currentTime);
    
    void deleteByUser(User user);
}

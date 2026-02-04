package com.raj.adoptme.service;

import com.raj.adoptme.entity.Otp;
import com.raj.adoptme.entity.User;
import com.raj.adoptme.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Value("${otp.expiration}")
    private Long otpExpiration;

    @Value("${otp.length}")
    private Integer otpLength;

    public String generateOtp(User user) {
        // Generate random 6-digit OTP
        String otpCode = String.format("%0" + otpLength + "d", new Random().nextInt((int) Math.pow(10, otpLength)));
        
        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(otpExpiration / 1000);
        
        // Create OTP entity
        Otp otp = Otp.builder()
                .otpCode(otpCode)
                .user(user)
                .expiresAt(expiresAt)
                .isUsed(false)
                .build();
        
        otpRepository.save(otp);
        
        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(User user, String otpCode) {
        return otpRepository.findByUserAndOtpCodeAndIsUsedFalseAndExpiresAtAfter(
                user, 
                otpCode, 
                LocalDateTime.now()
            )
            .map(otp -> {
                otp.setIsUsed(true);
                otp.setUsedAt(LocalDateTime.now());
                otpRepository.save(otp);
                return true;
            })
            .orElse(false);
    }

    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Transactional
    public void invalidateUserOtps(User user) {
        otpRepository.deleteByUser(user);
    }
}

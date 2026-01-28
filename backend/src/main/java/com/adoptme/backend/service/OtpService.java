package com.adoptme.backend.service;

import com.adoptme.backend.entity.OtpVerification;
import com.adoptme.backend.exception.BadRequestException;
import com.adoptme.backend.repository.OtpVerificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Transactional
    public void sendEmailOtp(String email) {
        // Delete any existing OTPs for this email
        otpRepository.deleteByEmail(email);

        // Generate new OTP
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        // Save OTP to database
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setType(OtpVerification.OtpType.EMAIL);
        otpVerification.setExpiresAt(expiresAt);
        otpVerification.setVerified(false);

        otpRepository.save(otpVerification);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp);
    }

    @Transactional
    public boolean verifyEmailOtp(String email, String otp) {
        OtpVerification otpVerification = otpRepository
                .findByEmailAndOtpAndVerifiedFalse(email, otp)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otpVerification.isExpired()) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Mark as verified
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        return true;
    }

    @Transactional
    public void cleanupExpiredOtps() {
        // This can be called by a scheduled task to clean up old OTPs
        // For now, we'll just delete old records when new ones are created
    }
}
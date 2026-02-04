package com.raj.adoptme.service;

import com.raj.adoptme.dto.AuthRequest;
import com.raj.adoptme.dto.AuthResponse;
import com.raj.adoptme.dto.OtpVerificationRequest;
import com.raj.adoptme.entity.User;
import com.raj.adoptme.repository.UserRepository;
import com.raj.adoptme.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public String sendOtp(AuthRequest request) {
        // Check if user exists, if not create new user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .name(request.getName())
                            .role(User.Role.ADOPTER)
                            .isVerified(false)
                            .isBlocked(false)
                            .build();
                    return userRepository.save(newUser);
                });

        // Check if user is blocked
        if (user.getIsBlocked()) {
            throw new RuntimeException("User account is blocked. Please contact support.");
        }

        // Invalidate any existing OTPs for this user
        otpService.invalidateUserOtps(user);

        // Generate new OTP
        String otpCode = otpService.generateOtp(user);

        // Send OTP via email
        boolean emailSent = emailService.sendOtpEmail(user.getEmail(), user.getName(), otpCode);

        if (!emailSent) {
            logger.error("Failed to send OTP email to: {}", user.getEmail());
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }

        logger.info("OTP sent successfully to: {}", user.getEmail());
        return "OTP sent successfully to " + user.getEmail();
    }

    @Transactional
    public AuthResponse verifyOtpAndLogin(OtpVerificationRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is blocked
        if (user.getIsBlocked()) {
            throw new RuntimeException("User account is blocked. Please contact support.");
        }

        // Verify OTP
        boolean isOtpValid = otpService.verifyOtp(user, request.getOtpCode());

        if (!isOtpValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Mark user as verified
        if (!user.getIsVerified()) {
            user.setIsVerified(true);
            userRepository.save(user);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
            user.getEmail(), 
            user.getRole().toString(), 
            user.getId()
        );

        logger.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().toString())
                .userId(user.getId())
                .message("Login successful")
                .build();
    }
}

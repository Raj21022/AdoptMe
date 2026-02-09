package com.adoptme.service;

import com.adoptme.dto.LoginRequest;
import com.adoptme.dto.LoginResponse;
import com.adoptme.dto.ResendOtpRequest;
import com.adoptme.dto.SignupRequest;
import com.adoptme.dto.VerifyOtpRequest;
import com.adoptme.entity.User;
import com.adoptme.exception.CustomException;
import com.adoptme.repository.UserRepository;
import com.adoptme.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private static final int OTP_EXPIRY_MINUTES = 10;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    public String signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        user.setVerified(false);
        user.setVerificationToken(generateOtp());
        user.setOtpExpiryAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), user.getVerificationToken());

        return "Signup successful. OTP sent to your email.";
    }

    public String verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (Boolean.TRUE.equals(user.getVerified())) {
            return "Email already verified. You can login.";
        }

        if (user.getOtpExpiryAt() == null || LocalDateTime.now().isAfter(user.getOtpExpiryAt())) {
            throw new CustomException("OTP expired. Please request a new OTP.");
        }

        if (user.getVerificationToken() == null || !user.getVerificationToken().equals(request.getOtp())) {
            throw new CustomException("Invalid OTP");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setOtpExpiryAt(null);
        userRepository.save(user);

        return "Email verified successfully. You can now login.";
    }

    public String resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (Boolean.TRUE.equals(user.getVerified())) {
            throw new CustomException("Email already verified. Please login.");
        }

        String otp = generateOtp();
        user.setVerificationToken(otp);
        user.setOtpExpiryAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return "A new OTP has been sent to your email.";
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or password"));

        if (!user.getVerified()) {
            throw new CustomException("Please verify your email with OTP before logging in");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private String generateOtp() {
        int otp = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(otp);
    }
}

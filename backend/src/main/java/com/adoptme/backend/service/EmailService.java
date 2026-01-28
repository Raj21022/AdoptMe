package com.adoptme.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("AdoptMe - Email Verification OTP");
            message.setText(buildOtpEmailBody(otp));
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String buildOtpEmailBody(String otp) {
        return "Welcome to AdoptMe!\n\n" +
               "Your email verification OTP is: " + otp + "\n\n" +
               "This OTP will expire in 10 minutes.\n\n" +
               "If you didn't request this, please ignore this email.\n\n" +
               "Best regards,\n" +
               "AdoptMe Team";
    }

    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to AdoptMe!");
            message.setText(buildWelcomeEmailBody(name));
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - welcome email is not critical
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    private String buildWelcomeEmailBody(String name) {
        return "Dear " + name + ",\n\n" +
               "Welcome to AdoptMe! Your email has been verified successfully.\n\n" +
               "You can now start posting animals for adoption or browse animals in your locality.\n\n" +
               "Thank you for joining us in making a difference for stray animals!\n\n" +
               "Best regards,\n" +
               "AdoptMe Team";
    }
}
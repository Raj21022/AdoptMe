package com.raj.adoptme.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public boolean sendOtpEmail(String toEmail, String userName, String otpCode) {
        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Your AdoptMe Verification Code";
        
        String htmlContent = buildOtpEmailTemplate(userName, otpCode);
        Content content = new Content("text/html", htmlContent);
        
        Mail mail = new Mail(from, subject, to, content);
        
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("OTP email sent successfully to: {}", toEmail);
                return true;
            } else {
                logger.error("Failed to send OTP email. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (IOException ex) {
            logger.error("Error sending OTP email to {}: {}", toEmail, ex.getMessage());
            return false;
        }
    }

    private String buildOtpEmailTemplate(String userName, String otpCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                ".container { background-color: white; max-width: 600px; margin: 0 auto; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; color: #4CAF50; margin-bottom: 30px; }" +
                ".otp-box { background-color: #f8f9fa; border: 2px dashed #4CAF50; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; }" +
                ".otp-code { font-size: 32px; font-weight: bold; color: #4CAF50; letter-spacing: 8px; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🐾 AdoptMe</h1>" +
                "</div>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>Welcome to AdoptMe! Your verification code is:</p>" +
                "<div class='otp-box'>" +
                "<div class='otp-code'>" + otpCode + "</div>" +
                "</div>" +
                "<p>This code will expire in <strong>5 minutes</strong>.</p>" +
                "<p>If you didn't request this code, please ignore this email.</p>" +
                "<div class='footer'>" +
                "<p>This is an automated message from AdoptMe. Please do not reply to this email.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

package com.adoptme.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    
    public void sendOtpEmail(String toEmail, String otp) {
        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Your AdoptMe verification code";
        Content content = new Content(
                "text/html",
                "<div style=\"font-family: Arial, sans-serif; background:#f7f6f2; padding:24px;\">" +
                "  <div style=\"max-width:560px; margin:0 auto; background:#ffffff; border-radius:16px; padding:24px; border:1px solid #e6e1d9;\">" +
                "    <div style=\"font-size:18px; font-weight:700; color:#1d3c34; margin-bottom:8px;\">AdoptMe</div>" +
                "    <div style=\"font-size:20px; font-weight:700; color:#1d3c34; margin-bottom:12px;\">Verify your email</div>" +
                "    <div style=\"color:#4b5b56; font-size:14px; line-height:1.6; margin-bottom:16px;\">" +
                "      Welcome to AdoptMe. Use the code below to complete your signup. It expires in 10 minutes." +
                "    </div>" +
                "    <div style=\"background:#f3faf7; border:1px dashed #b8e1d0; border-radius:12px; padding:16px; text-align:center;\">" +
                "      <div style=\"letter-spacing:6px; font-size:28px; font-weight:700; color:#27594c;\">" + otp + "</div>" +
                "      <div style=\"font-size:12px; color:#6b7a74; margin-top:6px;\">One-time verification code</div>" +
                "    </div>" +
                "    <div style=\"margin-top:16px; font-size:12px; color:#6b7a74;\">" +
                "      If you did not request this, you can safely ignore this email." +
                "    </div>" +
                "  </div>" +
                "</div>"
        );
        
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            
            // Log details for debugging
            System.out.println("SendGrid Status: " + response.getStatusCode());
            if (response.getStatusCode() >= 400) {
                System.err.println("SendGrid Error Body: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Critical error sending email: " + e.getMessage());
        }
    }
}

package com.project.app.service;

import com.project.app.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuditLogService auditLogService;

    public void processEmailRequest(EmailRequest emailRequest) {
        // Determine which type of email to send based on the EmailActionType
        switch (emailRequest.getEmailActionType()) {
            case SEND_PLAIN_EMAIL:
                sendPlainEmail(emailRequest);
                break;

//            case HTML:
//                sendHtmlEmail(emailRequest);
//                break;
//
//            case ATTACHMENT:
//                sendEmailWithAttachment(emailRequest);
//                break;
//
//            case SCHEDULE:
//                scheduleEmail(emailRequest);
//                break;

            default:
                throw new IllegalArgumentException("Invalid email action type");
        }
    }

    @Async
    public void sendPlainEmail(EmailRequest emailRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());
            message.setFrom(emailRequest.getFrom());

            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                message.setCc(emailRequest.getCc().toArray(new String[0]));
            }

            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                message.setBcc(emailRequest.getBcc().toArray(new String[0]));
            }

            log.info("Sending plain email to {}", emailRequest.getTo());
            mailSender.send(message);
            log.info("Plain email sent to {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Error sending plain email to {}: {}", emailRequest.getTo(), e.getMessage());
        }
    }
}

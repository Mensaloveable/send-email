package com.project.app.service;

import com.project.app.dto.EmailRequest;
import com.project.app.entity.User;
import com.project.app.enums.AuditAction;
import com.project.app.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public void processEmailRequest(EmailRequest emailRequest, String apiKey) {

        Optional<User> optionalUser = userRepository.findByApiKey(apiKey);
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException("User not found");

        User user = optionalUser.get();
        // Determine which type of email to send based on the EmailActionType
        switch (emailRequest.getEmailActionType()) {
            case SEND_PLAIN_EMAIL:
                sendPlainEmail(emailRequest, user);
                break;

            case SEND_HTML_EMAIL:
                sendHtmlEmail(emailRequest, user);
                break;
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
    public void sendPlainEmail(EmailRequest emailRequest, User user) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());
            message.setFrom(user.getEmail());

            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                message.setCc(emailRequest.getCc().toArray(new String[0]));
            }

            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                message.setBcc(emailRequest.getBcc().toArray(new String[0]));
            }

            log.info("Sending plain email to {}", emailRequest.getTo());
            auditLogService.log(AuditAction.SEND_EMAIL_WITH_HTML.getAction(), "User", user.getId(), "Send plain email");
            mailSender.send(message);
            log.info("Plain email sent to {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Error sending plain email to {}: {}", emailRequest.getTo(), e.getMessage());
        }
    }

    @Async
    protected void sendHtmlEmail(EmailRequest emailRequest, User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), true);
            helper.setFrom(user.getEmail());

            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc().toArray(new String[0]));
            }

            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                helper.setBcc(emailRequest.getBcc().toArray(new String[0]));
            }

            log.info("Sending HTML email to {}", emailRequest.getTo());
            mailSender.send(message);
            log.info("HTML email sent to {}", emailRequest.getTo());
        } catch (MessagingException e) {
            log.error("Error sending HTML email to {}: {}", emailRequest.getTo(), e.getMessage());
        }
    }
}

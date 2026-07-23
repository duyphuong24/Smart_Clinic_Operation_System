package com.smartclinic.notification.service;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final boolean emailEnabled;
    private final String fromEmail;

    public EmailServiceImpl(
            @Autowired(required = false) JavaMailSender mailSender,
            SpringTemplateEngine templateEngine,
            @Value("${smartclinic.notification.email-enabled:false}") boolean emailEnabled,
            @Value("${smartclinic.notification.from-email:noreply@smartclinic.com}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailEnabled = emailEnabled;
        this.fromEmail = fromEmail;
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (to == null || to.isBlank()) {
            log.info("[EmailService] Recipient email is blank. Skipping email dispatch.");
            return;
        }

        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }

        String htmlContent = templateEngine.process("mail/" + templateName, context);

        if (!emailEnabled || mailSender == null) {
            log.info("[EmailService Mock/Console Log] EMAIL DISABLED OR SMTP NOT CONFIGURED.");
            log.info("--> TO: {}", to);
            log.info("--> SUBJECT: {}", subject);
            log.info("--> CONTENT PREVIEW:\n{}", htmlContent.substring(0, Math.min(250, htmlContent.length())));
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("[EmailService] Email successfully sent to {}", to);
        } catch (Exception ex) {
            log.error("[EmailService] Failed to send email to {}: {}", to, ex.getMessage(), ex);
        }
    }
}

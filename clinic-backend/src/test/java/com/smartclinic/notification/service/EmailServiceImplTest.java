package com.smartclinic.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    private EmailServiceImpl enabledEmailService;
    private EmailServiceImpl disabledEmailService;

    @BeforeEach
    void setUp() {
        enabledEmailService = new EmailServiceImpl(mailSender, templateEngine, true, "noreply@smartclinic.com");
        disabledEmailService = new EmailServiceImpl(mailSender, templateEngine, false, "noreply@smartclinic.com");
    }

    @Test
    void sendHtmlEmailShouldDoNothingWhenRecipientIsBlank() {
        enabledEmailService.sendHtmlEmail("", "Subject", "template", Map.of());
        verify(templateEngine, never()).process(any(String.class), any(Context.class));
    }

    @Test
    void sendHtmlEmailShouldProcessTemplateAndSendEmailWhenEnabled() {
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        enabledEmailService.sendHtmlEmail("patient@example.com", "Test Subject", "appointment-confirmation", Map.of("key", "value"));

        verify(templateEngine).process(any(String.class), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlEmailShouldLogAndNotSendWhenDisabled() {
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html>Test</html>");

        disabledEmailService.sendHtmlEmail("patient@example.com", "Test Subject", "appointment-confirmation", Map.of("key", "value"));

        verify(templateEngine).process(any(String.class), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}

package apptive.devlog.domain.auth.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MailSender {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // multipart=false, encoding=UTF-8
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    /* multipart */ false,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, /* isHtml */ false);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSendException("메일 전송 실패", e);
        }
    }
}


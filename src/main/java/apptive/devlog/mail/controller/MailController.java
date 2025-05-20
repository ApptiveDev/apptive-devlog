package apptive.devlog.mail.controller;

import apptive.devlog.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @GetMapping("/simple")
    public void sendSimpleMailMessage(String email) {
        mailService.sendSimpleMailMessage(email);
    }

    @GetMapping("/html")
    public void sendMimeMessage(String email) {
        mailService.sendMimeMessage(email);
    }
}

package apptive.devlog.domain.auth.service;

import apptive.devlog.domain.auth.dto.EmailVerifyResponseDto;
import apptive.devlog.domain.auth.repository.VerifiedEmailRepository;
import apptive.devlog.domain.auth.util.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final MailSender mailSender;
    private final VerifiedEmailRepository verifiedEmailRepository;

    public void sendCode(String email) {
        String code = generateCode();
        verifiedEmailRepository.saveCode(email, code);
        mailSender.sendEmail(email, "[Devlog] 인증 코드", "인증 코드: " + code);
    }

    public EmailVerifyResponseDto verifyCode(String email, String code) {
        String storedCode = verifiedEmailRepository.getCode(email)
                .orElseThrow(() -> new IllegalArgumentException("코드가 만료되었거나 존재하지 않습니다."));
        if (!storedCode.equals(code)) throw new IllegalArgumentException("코드가 일치하지 않습니다.");
        verifiedEmailRepository.deleteCode(email);
        return new EmailVerifyResponseDto(verifiedEmailRepository.markVerified(email));
    }

    public boolean isVerified(String email, String token) {
        return verifiedEmailRepository.isVerified(email, token);
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }
}

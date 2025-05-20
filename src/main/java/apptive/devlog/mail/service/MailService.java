package apptive.devlog.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendSimpleMailMessage(String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        try{
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("메일 제목 : 작성 완료");
            simpleMailMessage.setText("내용 : 글 작성 완료");

            javaMailSender.send(simpleMailMessage);

            log.info("메일 발송 성공!");
        }
        catch (Exception e){
            log.info("메일 발송 실패!");
            throw new RuntimeException(e);
        }
    }

    public void sendMimeMessage(String email) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            //수신자
            mimeMessageHelper.setTo(email);

            mimeMessageHelper.setSubject("댓글 알림 메시지입니다.");

            String content = """
                    <!DOCTYPE html>
                        <html xmlns:th="http://www.thymeleaf.org">
                            <body>
                                <div style="margin:100px;">
                                    <h1> 댓글 알림 </h1>
                                    <br>
                                        <div align="center" style="border:1px solid black;">
                                            <h3> 작성하신 글에 댓글 또는 대댓글이 추가되었습니다. </h3>
                                        </div>
                                    <br/>
                                </div>
                            </body>
                        </html>
                    """;
            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessage);

            log.info("메일 발송 성공!");
        }
        catch (Exception e){
            log.info("메일 발송 실패!");
            throw new RuntimeException(e);
        }
    }
}

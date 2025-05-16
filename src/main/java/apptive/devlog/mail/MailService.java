package apptive.devlog.mail;

import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.comment.exception.NotFoundCommentException;
import apptive.devlog.comment.repository.CommentRepository;
import apptive.devlog.domain.Comment;
import apptive.devlog.domain.Follow;
import apptive.devlog.domain.Member;
import apptive.devlog.domain.Post;
import apptive.devlog.follow.exception.BadFollowRequestException;
import apptive.devlog.follow.repository.FollowRepository;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.post.exception.NotFoundPostException;
import apptive.devlog.post.repository.PostRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MailService {

    private final JavaMailSender mailSender;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendPostMail(Long postId, CommentResponse comment, String commentEmail) {
        log.info("메일 발송 시작");

        Post post = postRepository.findWithMember(postId).orElseThrow(() -> new NotFoundPostException("존재하는 게시글이 없습니다."));

        String receiveEmail = post.getMember().getEmail();

        if (commentEmail.equals(receiveEmail)) return; // 본인 게시글에 단 댓글은 전송이 안된다.

        String subject = "[devlog] " + post.getTitle() + " - 게시글에 댓글이 등록되었습니다.";
        String content = comment.getAuthor() + " : " + comment.getContent();

        sender(receiveEmail, subject, content);
    }

    public void sendCommentMail(Long commentId, CommentResponse reComment, String reCommentEmail) {
        Comment parentComment = commentRepository.findWithMember(commentId).
                orElseThrow(() -> new NotFoundCommentException("존재하는 댓글이 없습니다."));

        String receiveEmail = parentComment.getMember().getEmail();

        if (reCommentEmail.equals(receiveEmail)) return; // 본인 댓글에 작성한 대댓글이면 메일 보내지않음

        String subject = "[devlog] " + parentComment.getContent() + " - 댓글에 새로운 대댓글이 달렸습니다.";
        String content = reComment.getAuthor() + " : " + reComment.getContent();
        sender(receiveEmail,subject, content);

    }

    public void sendFollowReqMail(Long toMemberId, String fromMemberEmail) {

        Member toMember = memberRepository.findById(toMemberId).
                orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        Member fromMember = memberRepository.findByEmail(fromMemberEmail)
                .orElseThrow(() -> new NotFoundMemberException("존재하는 회원이 없습니다"));

        //의문점 : followService에서 이미 검증을 했는데 또 검증하는게 맞나?

        String content = fromMember.getNickname() + "님이 팔로우 요청을 보냈습니다.";
        sender(toMember.getEmail(), "[devlog] 팔로우 요청이 왔습니다.", content);
    }

    public void sendFollowResponseMail(Long followId) {
        Follow follow = followRepository.findByIdWithMember(followId)
                .orElseThrow(() -> new BadFollowRequestException("존재하는 팔로우 요청이 아닙니다."));

        String receiveEmail = follow.getFromMember().getEmail(); // 팔로우 요청한 사람 이름
        String name = follow.getToMember().getNickname(); // 팔로우 수락한 사람 이름
        String content = name + "님이 팔로우 요청을 수락하였습니다.";

        sender(receiveEmail, "[devlog] 팔로우 요청이 수락되었습니다.", content);
    }

    private void sender(String receiveEmail, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");
        try {
            log.info("받는 이메일 : {}", receiveEmail);
            log.info("보내는 이메일 : {}", fromMail);
            messageHelper.setTo(receiveEmail);
            messageHelper.setFrom(fromMail);
            messageHelper.setSubject(subject);
            messageHelper.setText(content);
            mailSender.send(message);
            log.info("메일 발송 성공");
        } catch (Exception e) {
            log.info("메일 발송 실패 {} ", e.getMessage()); // 메일 발송이 실패했다고 예외를 던질 필요는 없는 듯 함
        }
    }

}

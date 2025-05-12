package apptive.devlog.comment.service;

import apptive.devlog.comment.dto.CommentRequest;
import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.comment.repository.CommentRepository;
import apptive.devlog.domain.Comment;
import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.member.service.MemberService;
import apptive.devlog.post.dto.CreatePostRequest;
import apptive.devlog.post.dto.PostResponse;
import apptive.devlog.post.dto.PostWithCommentResponse;
import apptive.devlog.post.service.PostService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EntityManager em;

    @Autowired
    PostService postService;

    @Test
    void createComments() {
        Member member = makeMember();
        PostResponse post = postService.save(new CreatePostRequest("제목1", "내용1"), member.getEmail());

        for (int i=1; i<=100; i++) {
            CommentRequest comment = new CommentRequest("내용"+i);
            commentService.saveComment(comment,post.getId(),member.getEmail());
        }

        flushAndClear();

        PostWithCommentResponse findPost = postService.findPost(post.getId(), PageRequest.of(0,10));

        assertThat(findPost.getComments().size()).isEqualTo(10);
    }

    @Test
    void createReComments() {
        Member member = makeMember();
        PostResponse post = postService.save(new CreatePostRequest("제목1", "내용1"), member.getEmail());
        CommentRequest comment = new CommentRequest("내용1");
        CommentResponse response = commentService.saveComment(comment, post.getId(), member.getEmail());

        for (int i=1; i<=10; i++) {
            CommentResponse reComment = commentService.saveReComment(new CommentRequest("내용" + i), post.getId(), response.getId(), member.getEmail());
            System.out.println(reComment.getContent());
        }

        flushAndClear();

        Comment findComment = commentRepository.findById(response.getId()).get();



        assertThat(findComment.getComments().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("대댓글이 존재하는 부모댓글은 soft Delete")
    void deleteCommentWithChild() {
        Member member = makeMember();
        PostResponse post = postService.save(new CreatePostRequest("제목1", "내용1"), member.getEmail());
        CommentRequest comment = new CommentRequest("내용1");
        CommentResponse savedComment = commentService.saveComment(comment, post.getId(), member.getEmail());
        CommentRequest reComment = new CommentRequest("대댓글");
       commentService.saveReComment(reComment, post.getId(), savedComment.getId(), member.getEmail());

        flushAndClear();

        commentService.deleteComment(savedComment.getId(), member.getEmail());

        Comment findComment = commentRepository.findById(savedComment.getId()).get();

        assertThat(findComment.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("soft Delete 후 No Child인 Comment 자동 삭제")
    void softDeleteComment() {
        Member member = makeMember();
        PostResponse post = postService.save(new CreatePostRequest("제목1", "내용1"), member.getEmail());
        CommentRequest comment = new CommentRequest("내용1");
        CommentResponse savedComment = commentService.saveComment(comment, post.getId(), member.getEmail());
        CommentRequest reComment = new CommentRequest("대댓글");
        CommentResponse saveReComment = commentService.saveReComment(reComment, post.getId(), savedComment.getId(), member.getEmail());

        flushAndClear();

        commentService.deleteComment(savedComment.getId(), member.getEmail());
        commentService.deleteComment(saveReComment.getId(), member.getEmail());

        assertThat(commentRepository.findById(savedComment.getId())).isEmpty();
    }

    @Test
    void updateComment() {
        Member member = makeMember();
        PostResponse post = postService.save(new CreatePostRequest("제목1", "내용1"), member.getEmail());
        CommentRequest comment = new CommentRequest("내용1");
        CommentResponse savedComment = commentService.saveComment(comment, post.getId(), member.getEmail());
        commentService.updateComment(new CommentRequest("수정된 댓글"), savedComment.getId(), member.getEmail());

        flushAndClear();

        Comment updatedComment = commentRepository.findById(savedComment.getId()).get();

        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    private Member makeMember() {
        JoinForm joinForm = joinTestCase();
        memberService.join(joinForm);
        return memberRepository.findByNickname(joinForm.getNickname()).orElse(null);
    }

    private JoinForm joinTestCase() {
        JoinForm joinForm =
                new JoinForm("ljw2109@naver.com", "Qwer1234!!","Qwer1234!!", "이진원",
                        "이진원", LocalDate.of(2001,6,26), Gender.MALE);
        return joinForm;
    }

}
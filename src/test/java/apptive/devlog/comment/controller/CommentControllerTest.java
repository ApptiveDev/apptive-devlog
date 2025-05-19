package apptive.devlog.comment.controller;


import apptive.devlog.comment.dto.CommentRequest;
import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.comment.service.CommentService;
import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.member.service.MemberService;
import apptive.devlog.post.dto.CreatePostRequest;
import apptive.devlog.post.dto.PostResponse;
import apptive.devlog.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommentService commentService;

    @Autowired
    MemberService memberService;

    @Autowired
    PostService postService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createComment() throws Exception {
        String accessToken = makeAccessToken();

        CreatePostRequest postRequest = new CreatePostRequest("제목1", "내용1");

        PostResponse response = postService.save(postRequest, "ljw2109@naver.com");

        CommentRequest comment = new CommentRequest("댓글1");

        mockMvc.perform(post("/posts/{id}/comments", response.getId())
                .header("access", accessToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteComment() throws Exception {
        String accessToken = makeAccessToken();

        CreatePostRequest postRequest = new CreatePostRequest("제목1", "내용1");

        PostResponse response = postService.save(postRequest, "ljw2109@naver.com");

        CommentRequest comment = new CommentRequest("댓글1");

        CommentResponse savedComment = commentService.saveComment(comment, response.getId(), "ljw2109@naver.com");

        mockMvc.perform(delete("/comments/{id}",savedComment.getId())
            .header("access", accessToken)
        ).andExpect(status().isNoContent());
    }

    @Test
    void updateComment() throws Exception {
        String accessToken = makeAccessToken();

        CreatePostRequest postRequest = new CreatePostRequest("제목1", "내용1");

        PostResponse response = postService.save(postRequest, "ljw2109@naver.com");

        CommentRequest comment = new CommentRequest("댓글1");

        CommentResponse savedComment = commentService.saveComment(comment, response.getId(), "ljw2109@naver.com");

        CommentRequest updateComment = new CommentRequest("수정된 댓글");

        mockMvc.perform(put("/comments/{id}",savedComment.getId())
                .header("access", accessToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateComment))
        ).andExpect(status().isOk());
    }

    private String makeAccessToken() {
        JoinForm joinForm = joinTestCase();

        memberService.join(joinForm);
        return jwtUtil.createJWT("access", joinForm.getEmail(), "ROLE_MEMBER", 600000L);
    }


    private JoinForm joinTestCase() {
        JoinForm joinForm =
                new JoinForm("ljw2109@naver.com", "Qwer1234!!","Qwer1234!!", "이진원",
                        "이진원", LocalDate.of(2001,6,26), Gender.MALE);
        return joinForm;
    }

}
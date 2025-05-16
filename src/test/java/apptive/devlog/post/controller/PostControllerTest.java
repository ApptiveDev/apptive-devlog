package apptive.devlog.post.controller;

import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.repository.MemberRepository;
import apptive.devlog.member.service.MemberService;
import apptive.devlog.post.dto.CreatePostRequest;

import apptive.devlog.post.dto.PostResponse;
import apptive.devlog.post.dto.PostWithCommentResponse;
import apptive.devlog.post.dto.UpdatePostRequest;
import apptive.devlog.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostService postService;


    @Test
    void createPost() throws Exception {
        String access = makeAccessToken();

        CreatePostRequest post = new CreatePostRequest("제목1", "내용1", new ArrayList<>());

        mockMvc.perform(post("/users/me/post")
                .contentType("application/json").content(objectMapper.writeValueAsString(post))
                .header("access", access))
                .andExpect(status().isCreated());
    }

    @Test
    void getPosts() throws Exception {
        Member member = makeMember();
        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");

        PostResponse response = postService.save(post, member.getEmail());

        mockMvc.perform(get("/users/post/{id}",  response.getId()))
                .andExpect(status().isOk());

    }

    @Test
    void updatePost() throws Exception {
        String accessToken = makeAccessToken();
        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post,"ljw2109@naver.com");
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("제목2", "내용2");


        mockMvc.perform(patch("/users/me/post/{id}", response.getId())
                .header("access", accessToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updatePostRequest))
        ).andExpect(status().isOk());
    }

    @Test
    void updatePostFail() throws Exception {
        String accessToken = makeAccessToken();
        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post,"ljw2109@naver.com");
        UpdatePostRequest updatePostRequest = new UpdatePostRequest("실패", "실패");


        mockMvc.perform(patch("/users/me/post/{id}", response.getId())
                .header("access", accessToken)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updatePostRequest))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deletePost() throws Exception {
        String accessToken = makeAccessToken();
        CreatePostRequest post = new CreatePostRequest("제목1", "내용1");
        PostResponse response = postService.save(post,"ljw2109@naver.com");

        mockMvc.perform(delete("/users/me/post/{id}" ,response.getId())
                .header("access", accessToken))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("회원 페이지의 게시글 보기")
    void showMemberPosts() throws Exception {
        JoinForm joinForm = joinTestCase();
        memberService.join(joinForm);

        mockMvc.perform(get("/users/{nickname}/post", joinForm.getNickname())
                        .contentType("application/json")
                )
                .andExpect(status().isOk());
    }


    private String makeAccessToken() {
        JoinForm joinForm = joinTestCase();

        memberService.join(joinForm);
        return jwtUtil.createJWT("access", joinForm.getEmail(), "ROLE_MEMBER", 600000L);
    }

    private static JoinForm joinTestCase() {
        JoinForm joinForm =
                new JoinForm("ljw2109@naver.com", "Qwer1234!!","Qwer1234!!", "이진원",
                        "이진원", LocalDate.of(2001,6,26), Gender.MALE);
        return joinForm;
    }

    private Member makeMember() {
        JoinForm joinForm = joinTestCase();
        memberService.join(joinForm);
        return memberRepository.findByNickname(joinForm.getNickname()).orElse(null);
    }


}
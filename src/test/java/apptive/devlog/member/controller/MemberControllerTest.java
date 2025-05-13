package apptive.devlog.member.controller;

import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.domain.RefreshEntity;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.dto.LoginForm;
import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.dto.MemberUpdateForm;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.repository.RefreshRepository;
import apptive.devlog.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RefreshRepository refreshRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login() throws Exception {
        JoinForm joinForm = joinTestCase();

        LoginForm loginForm = new LoginForm(joinForm.getEmail(), joinForm.getPassword());

        String json = objectMapper.writeValueAsString(loginForm);

        mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"))
                .andExpect(cookie().exists("refresh"));
    }

    @Test
    void logout() throws Exception {

        JoinForm joinForm = joinTestCase();

        String refresh = jwtUtil.createJWT("refresh", joinForm.getEmail(), "ROLE_MEMBER", 86400000L);

        addRefreshEntity(joinForm.getEmail(), refresh, 86400000L);

        mockMvc.perform(post("/logout")
                        .cookie(createCookie("refresh",refresh)))
                .andExpect(status().isOk());
    }

    @Test
    void withdrawMember() throws Exception {
        JoinForm joinForm = joinTestCase();

        String access = jwtUtil.createJWT("access", joinForm.getEmail(), "ROLE_MEMBER", 600000L);

        mockMvc.perform(delete("/users/me")
                .contentType("application/json")
                .header("access", access))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMember() throws Exception {
        JoinForm joinForm = joinTestCase();

        String access = jwtUtil.createJWT("access", joinForm.getEmail(), "ROLE_MEMBER", 600000L);

        MemberUpdateForm updateForm = new MemberUpdateForm("이진원", "Qwer1234!!", "Qwer5678!!", "Qwer5678!!",
                LocalDate.of(2020, 1, 1), Gender.FEMALE);

        mockMvc.perform(patch("/users/me")
                .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateForm))
                .header("access", access))
                .andExpect(status().isOk());

        MemberDetails memberDetails= (MemberDetails) memberService.loadUserByUsername(joinForm.getEmail());
        Member findMember = memberDetails.getMember();

        assertThat(findMember.getNickname()).isEqualTo(updateForm.getNickname());
        assertThat(findMember.getGender()).isEqualTo(updateForm.getGender());
        assertThat(passwordEncoder.matches(updateForm.getConfirmPassword(), findMember.getPassword())).isTrue();
        assertThat(findMember.getBirthdate()).isEqualTo(updateForm.getBirthdate());
    }

    @Test
    void reissue() throws Exception {
        JoinForm joinForm = joinTestCase();

        String refresh = jwtUtil.createJWT("refresh", joinForm.getEmail(), "ROLE_MEMBER", 86400000L);

        addRefreshEntity(joinForm.getEmail(), refresh, 86400000L);

        mockMvc.perform(post("/reissue")
                        .contentType("application/json")
                        .cookie(createCookie("refresh", refresh)))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"))
                .andExpect(cookie().exists("refresh"));
    }

    @Test
    void reissueFail() throws Exception {

        mockMvc.perform(post("/reissue")
                        .contentType("application/json")
                        .cookie(new Cookie("fail", "fail")))
                .andExpect(status().isBadRequest());
    }

    private JoinForm joinTestCase() {
        JoinForm joinForm =
                new JoinForm("ljw2109@naver.com", "Qwer1234!!","Qwer1234!!", "이진원",
                        "이진원", LocalDate.of(2001,6,26), Gender.MALE);

        memberService.join(joinForm);
        return joinForm;
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity(email, refresh, date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        return cookie;
    }
}

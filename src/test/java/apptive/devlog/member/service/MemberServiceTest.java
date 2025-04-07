package apptive.devlog.member.service;

import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.domain.RefreshEntity;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.dto.LoginForm;
import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.dto.UpdateForm;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.exception.PasswordException;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    MemberService memberService;

    @Autowired
    RefreshRepository refreshRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void join() {
        JoinForm joinForm = joinTestCase();

        MemberDetails findMember = (MemberDetails) memberService.loadUserByUsername(joinForm.getEmail());

        assertThat(findMember.getUsername()).isEqualTo(joinForm.getEmail());
    }

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
    void updateMember() {
        JoinForm joinForm = joinTestCase();
        UpdateForm updateForm = new UpdateForm("이진원", "Qwer1234!!", "Qwer5678!!", "Qwer5678!!",
                LocalDate.of(2020, 1, 1), Gender.FEMALE);

        memberService.update(joinForm.getEmail(), updateForm);

        MemberDetails memberDetails= (MemberDetails) memberService.loadUserByUsername(joinForm.getEmail());
        Member findMember = memberDetails.getMember();

        assertThat(findMember.getNickname()).isEqualTo(updateForm.getNickname());
        assertThat(findMember.getGender()).isEqualTo(updateForm.getGender());
        assertThat(passwordEncoder.matches(updateForm.getConfirmPassword(), findMember.getPassword())).isTrue();
        assertThat(findMember.getBirthdate()).isEqualTo(updateForm.getBirthdate());
    }

    @Test
    void updateMemberException() {
        JoinForm joinForm = joinTestCase();
        UpdateForm updateForm = new UpdateForm("이진원", "Qwer1234!!", "Qwer56781!!", "Qwer5678!!",
                LocalDate.of(2020, 1, 1), Gender.FEMALE);


        assertThatThrownBy(()-> memberService.update(joinForm.getEmail(), updateForm))
                .isInstanceOf(PasswordException.class);
    }

    @Test
    void withdraw() {
        JoinForm joinForm = joinTestCase();
        memberService.withdraw(joinForm.getEmail());

        assertThatThrownBy(()->memberService.loadUserByUsername(joinForm.getEmail())).isInstanceOf(UsernameNotFoundException.class);
        assertThat(refreshRepository.existsByEmail(joinForm.getEmail())).isEqualTo(false);
    }

    @Test
    void withdrawException() {
        String email = "notExistEmail@naver.com";
        assertThatThrownBy(()->memberService.withdraw(email))
                .isInstanceOf(NotFoundMemberException.class);
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
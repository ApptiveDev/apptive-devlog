package apptive.devlog.member.service;

import apptive.devlog.domain.Gender;
import apptive.devlog.domain.Member;
import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.dto.MemberUpdateForm;
import apptive.devlog.member.exception.NotFoundMemberException;
import apptive.devlog.member.exception.PasswordException;
import apptive.devlog.member.repository.RefreshRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    RefreshRepository refreshRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    void join() {
        JoinForm joinForm = joinTestCase();

        MemberDetails findMember = (MemberDetails) memberService.loadUserByUsername(joinForm.getEmail());

        assertThat(findMember.getUsername()).isEqualTo(joinForm.getEmail());
    }


    @Test
    void updateMember() {
        JoinForm joinForm = joinTestCase();
        MemberUpdateForm updateForm = new MemberUpdateForm("이진원", "Qwer1234!!", "Qwer5678!!", "Qwer5678!!",
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
        MemberUpdateForm updateForm = new MemberUpdateForm("이진원", "Qwer1234!!", "Qwer56781!!", "Qwer5678!!",
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

    private JoinForm joinTestCase() {
        JoinForm joinForm =
                new JoinForm("ljw2109@naver.com", "Qwer1234!!","Qwer1234!!", "이진원",
                        "이진원", LocalDate.of(2001,6,26), Gender.MALE);

        memberService.join(joinForm);
        return joinForm;
    }

}
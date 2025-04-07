package apptive.devlog.member.controller;

import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.dto.LoginForm;
import apptive.devlog.member.dto.UpdateForm;
import apptive.devlog.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signUp(@Valid @RequestBody JoinForm form) {
        memberService.join(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입 성공"));
    }

    @PostMapping("/users/{email}")
    public ResponseEntity<Map<String,String>> withdraw(@PathVariable String email) {
        memberService.withdraw(email);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "회원탈퇴 성공"));
    }

    @PatchMapping("/users/{email}")
    public ResponseEntity<Map<String,String>> updateMember(@PathVariable String email, @RequestBody UpdateForm form) {

        memberService.update(email, form);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "회원정보 변경 성공"));
    }

}

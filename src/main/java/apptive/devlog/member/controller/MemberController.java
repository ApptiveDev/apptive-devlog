package apptive.devlog.member.controller;

import apptive.devlog.member.dto.JoinForm;
import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.dto.MemberUpdateForm;
import apptive.devlog.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/users")
    public ResponseEntity<Map<String,String>> signUp(@Valid @RequestBody JoinForm form) {
        memberService.join(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입 성공"));
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<Map<String,String>> withdraw(@AuthenticationPrincipal MemberDetails memberDetails) {

        memberService.withdraw(memberDetails.getUsername());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "회원탈퇴 성공"));
    }

    @PatchMapping("/users/me")
    public ResponseEntity<Map<String,String>> updateMember(@RequestBody MemberUpdateForm form,
                                                           @AuthenticationPrincipal MemberDetails memberDetails) {

        memberService.update(memberDetails.getUsername(), form);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "회원정보 변경 성공"));
    }

}

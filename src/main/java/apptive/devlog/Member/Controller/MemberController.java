package apptive.devlog.Member.Controller;

import apptive.devlog.Global.Auth.Dto.SignUpDto;
import apptive.devlog.Global.Response.Result.ResultCode;
import apptive.devlog.Global.Response.Result.ResultResponse;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Dto.UpdateProfileDto;
import apptive.devlog.Member.Dto.UpdateProfileRequest;
import apptive.devlog.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResultResponse> signup(@RequestBody SignUpDto signUpDto) {
        Member member = memberService.signUp(signUpDto);
        final ResultResponse response = new ResultResponse(ResultCode.REGISTER_SUCCESS,member);
        return new ResponseEntity<>(response,response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ResultResponse> updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest,@RequestHeader("Authorization") String authorizationHeader) throws AccessDeniedException {
        String token = authorizationHeader.replace("Bearer ", "");
        memberService.updateProfile(new UpdateProfileDto(updateProfileRequest.getNickname(),updateProfileRequest.getPassword(),token));
        final ResultResponse response = new ResultResponse(ResultCode.UPDATE_SUCCESS,ResultCode.UPDATE_SUCCESS.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
}

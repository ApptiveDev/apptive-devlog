package apptive.devlog.domain.auth.controller;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.domain.auth.dto.EmailSendRequestDto;
import apptive.devlog.domain.auth.dto.EmailVerifyRequestDto;
import apptive.devlog.domain.auth.dto.EmailVerifyResponseDto;
import apptive.devlog.domain.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> send(@Valid @RequestBody EmailSendRequestDto request) {
        emailVerificationService.sendCode(request.getEmail());
        return CommonResponse.ok(null);
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<EmailVerifyResponseDto> verify(@Valid @RequestBody EmailVerifyRequestDto request) {
        return CommonResponse.ok(emailVerificationService.verifyCode(request.getEmail(), request.getCode()));
    }
}

package apptive.devlog.domain.auth.controller;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.documentation.domain.auth.AuthLoginDoc;
import apptive.devlog.documentation.domain.auth.AuthLogoutDoc;
import apptive.devlog.documentation.domain.auth.AuthRefreshDoc;
import apptive.devlog.documentation.domain.auth.AuthSignupDoc;
import apptive.devlog.documentation.tags.AuthDocumentation;
import apptive.devlog.domain.auth.dto.*;
import apptive.devlog.domain.auth.service.AuthService;
import apptive.devlog.global.annotation.InjectToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthDocumentation {
    private final AuthService authService;

    @AuthSignupDoc
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<UserSignupResponseDto> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto responseDto = authService.signup(requestDto);
        return CommonResponse.created(responseDto);
    }

    @AuthLoginDoc
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<UserLoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto responseDto = authService.login(requestDto);
        return CommonResponse.ok(responseDto);
    }

    @AuthRefreshDoc
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<UserRefreshResponseDto> refresh(@Valid @InjectToken UserRefreshRequestDto requestDto) {
        UserRefreshResponseDto responseDto = authService.refresh(requestDto);
        return CommonResponse.ok(responseDto);
    }

    @AuthLogoutDoc
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> logout(@Valid @InjectToken UserLogoutRequestDto requestDto) {
        authService.logout(requestDto);
        return CommonResponse.noContent();
    }
}

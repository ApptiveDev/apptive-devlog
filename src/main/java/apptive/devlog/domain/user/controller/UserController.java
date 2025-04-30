package apptive.devlog.domain.user.controller;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.documentation.tags.UserDocumentation;
import apptive.devlog.documentation.domain.user.UserProfileDoc;
import apptive.devlog.domain.user.dto.UserProfileRequestDto;
import apptive.devlog.domain.user.dto.UserProfileResponseDto;
import apptive.devlog.domain.user.service.UserService;
import apptive.devlog.global.annotation.InjectEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserDocumentation {

    private final UserService userService;

    @UserProfileDoc
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<UserProfileResponseDto>> getUserProfile(@Valid @InjectEmail UserProfileRequestDto requestDto) {
        UserProfileResponseDto responseDto = userService.getUserProfile(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.ok(responseDto));
    }
}

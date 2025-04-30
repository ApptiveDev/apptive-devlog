package apptive.devlog.domain.oauth2.controller;

import apptive.devlog.domain.oauth2.dto.OAuth2CallbackRequestDto;
import apptive.devlog.domain.oauth2.dto.OAuth2CallbackResponseDto;
import apptive.devlog.domain.oauth2.service.OAuth2PkceService;
import apptive.devlog.common.response.success.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2PkceController {

    private final OAuth2PkceService pkceService;

    @PostMapping("/pkce/callback")
    public ResponseEntity<CommonResponse<OAuth2CallbackResponseDto>> handleCallback(@Valid @RequestBody OAuth2CallbackRequestDto requestDto) {
        OAuth2CallbackResponseDto responseDto = pkceService.handleCallback(requestDto);
        return ResponseEntity.ok(CommonResponse.ok(responseDto));
    }
}

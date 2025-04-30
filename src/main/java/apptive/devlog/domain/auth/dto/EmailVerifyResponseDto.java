package apptive.devlog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이메일 인증 응답 DTO")
public class EmailVerifyResponseDto {
    @Schema(description = "이메일 인증 토큰", example = "eyJhbGciOiJIUzI1...")
    private String token;
}

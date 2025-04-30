package apptive.devlog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "액세스/리프레시 토큰 응답 DTO")
public class UserRefreshResponseDto {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token", example = "dGhpc19pc19hX3JlZnJlc2hfdG9rZW4=")
    private String refreshToken;
}
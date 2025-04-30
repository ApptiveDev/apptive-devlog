package apptive.devlog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 로그아웃 요청 DTO")
public class UserLogoutRequestDto {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "Access Token은 필수입니다.")
    private String accessToken;

    @Schema(description = "Refresh Token", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}

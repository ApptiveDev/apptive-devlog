package apptive.devlog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Access Token과 Refresh Token 재발급 요청 DTO")
public class UserRefreshRequestDto {
    @NotBlank
    @Schema(description = "기존 Access Token", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String accessToken;

    @NotBlank
    @Schema(description = "Refresh Token", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
    private String refreshToken;
}

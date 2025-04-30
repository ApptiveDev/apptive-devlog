package apptive.devlog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class UserLoginRequestDto {
    @Schema(description = "사용자 이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email
    @NotBlank
    private String email;

    @Schema(description = "사용자 비밀번호", example = "securePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;
}

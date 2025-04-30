package apptive.devlog.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "유저 프로필 요청 DTO")
public class UserProfileRequestDto {
    @Schema(description = "유저 이메일", example = "user@example.com", required = true)
    @NotBlank
    private String email;
}

package apptive.devlog.domain.auth.dto;

import apptive.devlog.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "회원가입 결과 응답")
@Getter
public class UserSignupResponseDto {
    @Schema(description = "가입된 유저의 이메일", example = "test@example.com")
    private final String email;

    public UserSignupResponseDto(User user) {
        this.email = user.getEmail();
    }
}

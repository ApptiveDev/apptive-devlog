package apptive.devlog.domain.auth.dto;

import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.enums.Gender;
import apptive.devlog.domain.user.enums.Provider;
import apptive.devlog.domain.user.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class UserSignupRequestDto {
    @Schema(description = "이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email
    @NotBlank
    private String email;

    @Schema(description = "비밀번호 (8자 이상 20자 이하)", example = "securePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 8, max = 20)
    @NotBlank
    private String password;

    @Schema(description = "이메일 인증 토큰", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String token;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    @Schema(description = "닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birth;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    public User toEntity(PasswordEncoder passwordEncoder) {
        User user = User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .birth(birth)
                .gender(gender)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();
        user.addProvider(Provider.LOCAL);
        return user;
    }
}

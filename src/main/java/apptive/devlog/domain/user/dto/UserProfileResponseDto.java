package apptive.devlog.domain.user.dto;

import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "유저 프로필 응답 DTO")
public class UserProfileResponseDto {
    @Schema(description = "이메일", example = "user@example.com")
    private final String email;
    @Schema(description = "닉네임", example = "nickname123")
    private final String nickname;
    @Schema(description = "이름", example = "홍길동")
    private final String name;
    @Schema(description = "생일", example = "1995-05-20")
    private final LocalDate birthday;
    @Schema(description = "성별", example = "MALE")
    private final Gender gender;

    public UserProfileResponseDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.birthday = user.getBirth();
        this.gender = user.getGender();
    }
}

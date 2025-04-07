package apptive.devlog.auth.dto;


import apptive.devlog.auth.entity.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserSaveForm {
    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{10,}$",
            message = "10자 이상으로 구성하세요. 대소문자와 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotNull(message = "생년월일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotNull(message = "성별은 필수입니다.")
    @Enumerated(EnumType.STRING)
    private Gender gender;

}

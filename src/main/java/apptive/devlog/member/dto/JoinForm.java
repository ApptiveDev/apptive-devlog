package apptive.devlog.member.dto;

import apptive.devlog.domain.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JoinForm {

    @Email(message = "이메일 형식에 맞게 입력해주세요") //이메일 형식 검증
    @NotBlank(message = "이메일을 입력해주세요") // 만약 null이나 빈 문자열이 넘어오면 검증 자체를 못한다.
    private String email;

    @Size(min = 10, max = 20, message = "비밀번호는 10자이상 20자이하로 입력해주세요")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()_+=\\-\\[\\]{}|\\\\:;\"'<>,.?/]).{10,20}$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @Size(min = 10, max = 20, message = "비밀번호는 10자이상 20자이하로 입력해주세요")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()_+=\\-\\[\\]{}|\\\\:;\"'<>,.?/]).{10,20}$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String confirmPassword;

    @NotBlank(message = "이름을 입력해주세요")
    private String username;

    @NotBlank(message = "별명을 입력해주세요")
    private String nickname;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @NotNull(message = "생년월일을 입력해주세요")
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "성별을 입력해주세요")
    private Gender gender;

    public JoinForm(String email, String password, String confirmPassword, String username, String nickname, LocalDate birthdate, Gender gender) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.username = username;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    protected JoinForm() {}
}

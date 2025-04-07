package apptive.devlog.member.dto;


import apptive.devlog.domain.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateForm {


    @NotBlank(message = "별명을 입력해주세요")
    private String nickname;

    @Size(min = 10, max = 20, message = "비밀번호는 10자이상 20자이하로 입력해주세요")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()_+=\\-\\[\\]{}|\\\\:;\"'<>,.?/]).{10,20}$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String currentPassword;

    @Size(min = 10, max = 20, message = "비밀번호는 10자이상 20자이하로 입력해주세요")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()_+=\\-\\[\\]{}|\\\\:;\"'<>,.?/]).{10,20}$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;

    @Size(min = 10, max = 20, message = "비밀번호는 10자이상 20자이하로 입력해주세요")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()_+=\\-\\[\\]{}|\\\\:;\"'<>,.?/]).{10,20}$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String confirmPassword;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @NotNull(message = "생년월일을 입력해주세요")
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "성별을 입력해주세요")
    private Gender gender;


    public UpdateForm(String nickname, String currentPassword, String newPassword, String confirmPassword, LocalDate birthdate, Gender gender) {
        this.nickname = nickname;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    protected UpdateForm() {}

}

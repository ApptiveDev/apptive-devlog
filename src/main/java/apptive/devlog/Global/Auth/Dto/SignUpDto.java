package apptive.devlog.Global.Auth.Dto;

import apptive.devlog.Global.Enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String birth;
    private Gender gender;
}

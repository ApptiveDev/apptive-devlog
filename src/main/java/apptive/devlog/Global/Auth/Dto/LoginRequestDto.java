package apptive.devlog.Global.Auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDto {
    String email;
    String password;

}

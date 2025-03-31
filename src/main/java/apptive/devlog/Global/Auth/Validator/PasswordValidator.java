package apptive.devlog.Global.Auth.Validator;


import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public boolean isValid(String password) {
        if (password == null || !(password.length() >= 10)) return false; //길이 10자 이상
        if (!password.matches(".*[A-Z].*")) return false; //대문자
        if (!password.matches(".*[a-z].*")) return false; //소문자
        if (!password.matches(".*[0-9].*")) return false; //숫자
        if (!password.matches(".*[!@#$%^&*()\\-_+=\\[\\]{};:'\",.<>/?].*")) return false; // 허용된 특수문자 포함
        if (password.contains(" ")) return false; //공백 x
        return true;
    }
}

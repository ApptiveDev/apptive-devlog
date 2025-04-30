package apptive.devlog.common.response.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 이메일 인증 토큰이 유효하지 않을 경우 발생하는 예외
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
public class InvalidEmailVerificationTokenException extends RuntimeException {

    public InvalidEmailVerificationTokenException() {
        super("유효하지 않은 이메일 인증 토큰입니다.");
    }

    public InvalidEmailVerificationTokenException(String message) {
        super(message);
    }
}

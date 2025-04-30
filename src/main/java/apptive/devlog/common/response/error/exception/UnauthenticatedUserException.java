package apptive.devlog.common.response.error.exception;

public class UnauthenticatedUserException extends RuntimeException {

    public UnauthenticatedUserException() {
        super("인증되지 않은 사용자입니다.");
    }

    public UnauthenticatedUserException(String message) {
        super(message);
    }

    public UnauthenticatedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

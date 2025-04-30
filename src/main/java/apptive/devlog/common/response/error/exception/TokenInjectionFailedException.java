package apptive.devlog.common.response.error.exception;

public class TokenInjectionFailedException extends RuntimeException {

    public TokenInjectionFailedException(String message) {
        super("[Token 주입 실패] " + message);
    }

    public TokenInjectionFailedException(String fieldName, Throwable cause) {
        super(String.format("[Token 주입 실패] 필드 '%s'에 접근 중 오류 발생", fieldName), cause);
    }

    public TokenInjectionFailedException(Throwable cause) {
        super("[Token 주입 실패] 원인 미상", cause);
    }
}

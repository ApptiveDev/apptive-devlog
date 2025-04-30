package apptive.devlog.common.response.error.exception;

public class InvalidOAuth2ResponseException extends RuntimeException {
    public InvalidOAuth2ResponseException(String key) {
        super("OAuth2 응답에서 예상한 '" + key + "' 구조가 올바르지 않습니다.");
    }
}

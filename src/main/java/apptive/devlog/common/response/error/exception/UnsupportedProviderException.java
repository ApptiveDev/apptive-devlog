package apptive.devlog.common.response.error.exception;

public class UnsupportedProviderException extends RuntimeException {
    public UnsupportedProviderException(String provider) {
        super("지원하지 않는 OAuth2 Provider입니다: " + provider);
    }
}

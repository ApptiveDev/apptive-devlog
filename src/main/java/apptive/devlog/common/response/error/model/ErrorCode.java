package apptive.devlog.common.response.error.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    USER_NOT_FOUND(404, "USER_001", "해당 사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(400, "USER_002", "이미 존재하는 이메일입니다."),
    INVALID_PROVIDER(400, "USER_003", "유효하지 않은 OAuth Provider입니다."),
    INVALID_PASSWORD(401, "USER_004", "비밀번호가 일치하지 않습니다."),
    UNAUTHENTICATED_USER(401, "USER_005", "사용자 인증 정보가 없습니다."),
    INVALID_EMAIL_OR_PASSWORD(401, "USER_006", "이메일 또는 비밀번호가 잘못되었습니다."),

    INVALID_STATE_FORMAT(400, "PKCE_001", "State 파라미터 형식이 올바르지 않습니다."),
    OAUTH2_TOKEN_REQUEST_FAILED(500, "PKCE_002", "OAuth2 토큰 요청에 실패했습니다."),
    OAUTH2_USERINFO_FAILED(500, "PKCE_003", "OAuth2 사용자 정보 요청에 실패했습니다."),

    INVALID_TOKEN(401, "TOKEN_001", "유효하지 않은 토큰입니다."),
    TOKEN_INJECTION_FAILED(500, "TOKEN_002", "토큰 주입에 실패했습니다."),
    INVALID_REFRESH_TOKEN(401, "TOKEN_003", "유효하지 않은 리프레시 토큰입니다."),

    UNKNOWN_ERROR(500, "ERROR_001", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}

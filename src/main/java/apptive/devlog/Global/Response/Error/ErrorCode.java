package apptive.devlog.Global.Response.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "C001", "internal server error"),
    INVALID_INPUT_VALUE(400, "C002", "invalid input type"),
    METHOD_NOT_ALLOWED(405, "C003", "method not allowed"),
    INVALID_TYPE_VALUE(400, "C004", "invalid type value"),
    BAD_CREDENTIALS(400, "C005", "bad credentials"),

    INVALID_REQUEST(403, "M003", "잘못된 요청입니다."),

    // Member
    NOT_EXIST(404, "M001", "Not exist"),
    EMAIL_DUPLICATION(400, "M002", "이미 사용중인 이메일입니다."),
    NO_AUTHORITY(403, "M003", "권한이 없습니다."),
    NEED_LOGIN(401, "M004", "로그인이 필요합니다."),
    AUTHENTICATION_NOT_FOUND(401, "M005", "Security Context에 인증 정보가 없습니다."),
    MEMBER_ALREADY_LOGOUT(400, "M006", "이미 로그아웃된 유저입니다."),
    NICKNAME_DUPLICATION(400, "M007", "이미 사용중인 닉네임입니다."),
    INVALID_TOKEN(403, "M008", "유효하지않은 토큰입니다."),
    MEMBER_NOT_EXIST(403, "M009", "존재하지 않는 유저입니다."),
    // Auth
    REFRESH_TOKEN_INVALID(400, "A001", "refresh token invalid.");

    private int status;
    private final String code;
    private final String message;
}
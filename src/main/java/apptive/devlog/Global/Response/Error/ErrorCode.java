package apptive.devlog.Global.Response.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "internal server error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "invalid input type"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "method not allowed"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "invalid type value"),
    BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "C005", "bad credentials"),

    // Member
    NOT_EXIST(HttpStatus.NOT_FOUND, "M001", "Not exist"),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M002", "이미 사용중인 이메일입니다."),
    NO_AUTHORITY(HttpStatus.FORBIDDEN, "M003", "권한이 없습니다."),
    NEED_LOGIN(HttpStatus.UNAUTHORIZED, "M004", "로그인이 필요합니다."),
    AUTHENTICATION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "M005", "Security Context에 인증 정보가 없습니다."),
    MEMBER_ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "M006", "이미 로그아웃된 유저입니다."),
    NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "M007", "이미 사용중인 닉네임입니다."),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "M008", "유효하지않은 토큰입니다."),
    MEMBER_NOT_EXIST(HttpStatus.FORBIDDEN, "M009", "존재하지 않는 유저입니다."),
    // Auth
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "A001", "refresh token invalid."),

    // 게시글 관련 에러 코드
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "존재하지 않는 게시글입니다."),
    POST_NO_PERMISSION(HttpStatus.FORBIDDEN, "P002", "게시글 접근 권한이 없습니다."),
    POST_TITLE_BLANK(HttpStatus.BAD_REQUEST, "P003", "제목은 공백일 수 없습니다."),
    POST_CONTENT_BLANK(HttpStatus.BAD_REQUEST, "P004", "내용은 공백일 수 없습니다."),

    // 댓글 관련 에러 코드
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 댓글입니다."),
    COMMENT_NO_PERMISSION(HttpStatus.FORBIDDEN, "C002", "댓글 접근 권한이 없습니다."),
    COMMENT_CONTENT_BLANK(HttpStatus.BAD_REQUEST, "C003", "댓글 내용은 공백일 수 없습니다."),

    // 대댓글 관련 에러 코드
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 대댓글입니다."),
    REPLY_NO_PERMISSION(HttpStatus.FORBIDDEN, "R002", "대댓글 접근 권한이 없습니다."),
    REPLY_CONTENT_BLANK(HttpStatus.BAD_REQUEST, "R003", "대댓글 내용은 공백일 수 없습니다.");

    private HttpStatus status;
    private final String code;
    private final String message;
}
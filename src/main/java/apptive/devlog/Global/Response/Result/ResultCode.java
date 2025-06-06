package apptive.devlog.Global.Response.Result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResultCode {

    // Member
    REGISTER_SUCCESS(HttpStatus.CREATED, "M001", "회원가입 되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "M002", "로그인 되었습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, "M003", "재발급 되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "M004", "로그아웃 되었습니다."),
    GET_MY_INFO_SUCCESS(HttpStatus.OK, "M005", "내 정보 조회 완료"),

    UPDATE_SUCCESS(HttpStatus.OK, "M006", "업데이트 완료"),
    DELETE_SUCCESS(HttpStatus.OK, "M007", "삭제 완료"),

    //게시글 관련 result
    POST_SUCCESS(HttpStatus.CREATED, "P001", "게시글 작성에 성공하였습니다."),

    POST_DELETE_SUCCESS(HttpStatus.OK, "P002", "게시글을 성공적으로 삭제하였습니다."),

    POST_READ_SUCCESS(HttpStatus.OK, "P003", "게시글을 성공적으로 조회하였습니다."),
    POST_UPDATE_SUCCESS(HttpStatus.OK, "P004", "게시글을 성공적으로 수정하였습니다."),

    //댓글 관련 result
    COMMENT_SUCCESS(HttpStatus.CREATED, "C001", "댓글 작성에 성공하였습니다."),

    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "C002", "댓글을 성공적으로 삭제하였습니다."),

    REPLY_SUCCESS(HttpStatus.CREATED, "C003", "대댓글 작성에 성공하였습니다."),

    REPLY_DELETE_SUCCESS(HttpStatus.OK, "C004", "대댓글을 성공적으로 삭제하였습니다.");


    private HttpStatus status;
    private final String code;
    private final String message;
}

package apptive.devlog.Global.Response.Error;

import apptive.devlog.Global.Exception.*;
import apptive.devlog.Global.Response.Result.ResultResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static apptive.devlog.Global.Response.Error.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException e) {
        final ErrorResponse response = ErrorResponse.of(BAD_CREDENTIALS);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        final ErrorResponse response = ErrorResponse.of(NOT_EXIST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        final ErrorResponse response = ErrorResponse.of(NO_AUTHORITY, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        final ErrorResponse response = ErrorResponse.of(METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // @Valid, @Validated 에서 binding error 발생 시 (@RequestBody)
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, response.getStatus());
    }

    //이메일 중복 예외 코드 핸들링
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleEmailDuplicationException(EmailDuplicationException e) {
        final ErrorResponse response = ErrorResponse.of(EMAIL_DUPLICATION, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    // 닉네임 중복 예외 코드 핸들링
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleNicknameDuplicationException(NicknameDuplicationException e) {
        final ErrorResponse response = ErrorResponse.of(NICKNAME_DUPLICATION, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        final ErrorResponse response = ErrorResponse.of(INVALID_TOKEN, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMemberNotExistException(MemberNotExistException e) {
        final ErrorResponse response = ErrorResponse.of(MEMBER_NOT_EXIST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMemberAlreadyLogoutException(MemberAlreadyLogoutException e) {
        final ErrorResponse response = ErrorResponse.of(MEMBER_ALREADY_LOGOUT, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    // 게시글 관련 핸들러
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(POST_NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePostNoPermissionException(PostNoPermissionException e) {
        final ErrorResponse response = ErrorResponse.of(POST_NO_PERMISSION, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePostTitleBlankException(PostTitleBlankException e) {
        final ErrorResponse response = ErrorResponse.of(POST_TITLE_BLANK, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePostContentBlankException(PostContentBlankException e) {
        final ErrorResponse response = ErrorResponse.of(POST_CONTENT_BLANK, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    //댓글관련 핸들러
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(COMMENT_NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleCommentNoPermissionException(CommentNoPermissionException e) {
        final ErrorResponse response = ErrorResponse.of(COMMENT_NO_PERMISSION, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleCommentContentBlankException(CommentContentBlankException e) {
        final ErrorResponse response = ErrorResponse.of(COMMENT_CONTENT_BLANK, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    //대댓글 관련 핸들러
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleReplyNotFoundException(ReplyNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(REPLY_NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleReplyNoPermissionException(ReplyNoPermissionException e) {
        final ErrorResponse response = ErrorResponse.of(REPLY_NO_PERMISSION, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleReplyContentBlankException(ReplyContentBlankException e) {
        final ErrorResponse response = ErrorResponse.of(REPLY_CONTENT_BLANK, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }


    // 그 밖에 발생하는 모든 예외처리가 이곳으로 모인다.
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: ", e);
        final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
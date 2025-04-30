package apptive.devlog.common.response.error.handler;

import apptive.devlog.common.response.error.exception.*;
import apptive.devlog.common.response.error.model.ErrorCode;
import apptive.devlog.common.response.error.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 응답 빌더 (기본 메시지 사용)
    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(
                        errorCode.getStatus(),
                        errorCode.getMessage(),
                        errorCode.getCode()));
    }

    // 공통 응답 빌더 (커스텀 메시지 사용)
    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String customMessage) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(
                        errorCode.getStatus(),
                        customMessage,
                        errorCode.getCode()));
    }

    // 공통 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.warn("CustomException [{}]: {}", ex.getErrorCode().name(), ex.getMessage(), ex);
        return buildResponse(ex.getErrorCode());
    }

    // DTO 바인딩 예외 처리 (직접 만든 예외 + Spring 기본 예외)
    @ExceptionHandler({DtoBindingFailedException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBindingException(Exception ex) {
        log.warn("BindingException: {}", ex.getMessage(), ex);
        return buildResponse(ErrorCode.INVALID_STATE_FORMAT, ex.getMessage());
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        "BAD_REQUEST"));
    }

    // 런타임 예외 처리 (디버깅 및 추적용)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        "INTERNAL_ERROR"));
    }

    // 마지막 처리: 예상하지 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return buildResponse(ErrorCode.UNKNOWN_ERROR);
    }
}

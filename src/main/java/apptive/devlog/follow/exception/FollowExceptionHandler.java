package apptive.devlog.follow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class FollowExceptionHandler {

    @ExceptionHandler(BadFollowRequestException.class)
    public ResponseEntity<Map<String,String>> handleBadFollowRequestException(BadFollowRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",ex.getMessage()));
    }
}

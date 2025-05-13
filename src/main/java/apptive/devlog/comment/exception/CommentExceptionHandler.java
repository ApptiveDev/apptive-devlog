package apptive.devlog.comment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(BadCommentRequestException.class)
    public ResponseEntity<Map<String,String>> handleBadComment(BadCommentRequestException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundCommentException.class)
    public ResponseEntity<Map<String,String>> handleNotFoundComment(NotFoundCommentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }
}

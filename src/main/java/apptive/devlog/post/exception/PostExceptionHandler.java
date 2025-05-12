package apptive.devlog.post.exception;


import apptive.devlog.fileupload.exception.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<Map<String,String>> notFound(NotFoundPostException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(BadPostRequestException.class)
    public ResponseEntity<Map<String,String>> bad(BadPostRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String,String>> fileUploadEx(FileUploadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
    }
}

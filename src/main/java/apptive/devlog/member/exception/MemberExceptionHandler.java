package apptive.devlog.member.exception;

import apptive.devlog.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> beanValidationHandler(MethodArgumentNotValidException ex) {

        Map<String,String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(e-> e.getField(), e-> e.getDefaultMessage(),
                        (existing, replacement) -> existing));

        ErrorMessage errorMessage = new ErrorMessage(messages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<ErrorMessage> duplicateMemberHandler(DuplicateMemberException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorMessage());
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<Map<String,String>> notFoundMemberHandler(NotFoundMemberException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Map<String,String>> passwordHandler(PasswordException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",ex.getMessage()));
    }

    @ExceptionHandler(RefreshTokenValidateException.class)
    public ResponseEntity<Map<String,String>> refreshTokenValidateHandler(RefreshTokenValidateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",ex.getMessage()));
    }

}

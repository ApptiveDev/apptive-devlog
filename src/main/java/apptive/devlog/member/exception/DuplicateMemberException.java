package apptive.devlog.member.exception;

import apptive.devlog.error.ErrorMessage;
import lombok.Getter;

@Getter
public class DuplicateMemberException extends RuntimeException {

    private ErrorMessage errorMessage;
    public DuplicateMemberException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}

package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;
import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;


    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.POST_NOT_FOUND;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
} 
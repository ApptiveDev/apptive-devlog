package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class InvalidTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN.getMessage());
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

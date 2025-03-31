package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class InvalidRequestException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST.getMessage());
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

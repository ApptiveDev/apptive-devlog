package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class EmailDuplicationException extends RuntimeException{
    private final ErrorCode errorCode;

    public EmailDuplicationException() {
        super(ErrorCode.EMAIL_DUPLICATION.getMessage());
        this.errorCode = ErrorCode.EMAIL_DUPLICATION;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

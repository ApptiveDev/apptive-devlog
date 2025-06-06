package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class ReplyNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ReplyNotFoundException() {
        super(ErrorCode.REPLY_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.REPLY_NOT_FOUND;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

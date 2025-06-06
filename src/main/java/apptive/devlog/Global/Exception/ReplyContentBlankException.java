package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class ReplyContentBlankException extends RuntimeException {
    private final ErrorCode errorCode;

    public ReplyContentBlankException() {
        super(ErrorCode.REPLY_CONTENT_BLANK.getMessage());
        this.errorCode = ErrorCode.REPLY_CONTENT_BLANK;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

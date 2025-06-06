package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class CommentNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND.getMessage());
        this.errorCode = ErrorCode.COMMENT_NOT_FOUND;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

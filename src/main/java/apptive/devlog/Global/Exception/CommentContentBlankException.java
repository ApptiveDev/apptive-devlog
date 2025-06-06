package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class CommentContentBlankException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommentContentBlankException() {
        super(ErrorCode.COMMENT_CONTENT_BLANK.getMessage());
        this.errorCode = ErrorCode.COMMENT_CONTENT_BLANK;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

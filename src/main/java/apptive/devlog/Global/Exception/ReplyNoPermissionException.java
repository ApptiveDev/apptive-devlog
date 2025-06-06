package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class ReplyNoPermissionException extends RuntimeException {
    private final ErrorCode errorCode;

    public ReplyNoPermissionException() {
        super(ErrorCode.REPLY_NO_PERMISSION.getMessage());
        this.errorCode = ErrorCode.REPLY_NO_PERMISSION;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

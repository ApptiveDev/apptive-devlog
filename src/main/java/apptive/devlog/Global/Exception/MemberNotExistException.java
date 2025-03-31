package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class MemberNotExistException extends RuntimeException {
    private final ErrorCode errorCode;

    public MemberNotExistException() {
        super(ErrorCode.MEMBER_NOT_EXIST.getMessage());
        this.errorCode = ErrorCode.MEMBER_NOT_EXIST;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

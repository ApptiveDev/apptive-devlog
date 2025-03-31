package apptive.devlog.Global.Exception;

import apptive.devlog.Global.Response.Error.ErrorCode;

public class MemberAlreadyLogoutException extends RuntimeException {
    private final ErrorCode errorCode;

    public MemberAlreadyLogoutException() {
        super(ErrorCode.MEMBER_ALREADY_LOGOUT.getMessage());
        this.errorCode = ErrorCode.MEMBER_ALREADY_LOGOUT;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class InvalidRefreshTokenException extends CustomException {
    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
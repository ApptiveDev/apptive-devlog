package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}

package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class InvalidStateFormatException extends CustomException {
    public InvalidStateFormatException() {
        super(ErrorCode.INVALID_STATE_FORMAT);
    }
}
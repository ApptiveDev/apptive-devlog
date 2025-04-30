package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class InvalidProviderException extends CustomException {
    public InvalidProviderException() {
        super(ErrorCode.INVALID_PROVIDER);
    }
}

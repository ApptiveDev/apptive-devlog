package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class DuplicateEmailException extends CustomException {
    public DuplicateEmailException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}

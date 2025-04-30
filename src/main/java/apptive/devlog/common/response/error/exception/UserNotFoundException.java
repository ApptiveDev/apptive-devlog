package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}

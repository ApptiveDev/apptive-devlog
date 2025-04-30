package apptive.devlog.common.response.error.exception;

import apptive.devlog.common.response.error.model.ErrorCode;

public class OAuth2UserInfoException extends CustomException {
    public OAuth2UserInfoException(String message) {
        super(ErrorCode.OAUTH2_USERINFO_FAILED, message);
    }
}
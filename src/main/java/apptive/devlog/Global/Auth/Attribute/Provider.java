package apptive.devlog.Global.Auth.Attribute;

import apptive.devlog.Global.Exception.InvalidRequestException;

public enum Provider {

    DEVLOG,
    GOOGLE,
    KAKAO,
    NAVER;

    public static Provider from(String name) {
        try {
            return Provider.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException();
        }
    }
}


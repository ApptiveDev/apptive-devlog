package apptive.devlog.Global.Auth.Attribute;

public enum Provider {

    DEVLOG,
    google,
    kakao,
    naver;

    public static Provider from(String name) {
        try {
            return Provider.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
    }
}


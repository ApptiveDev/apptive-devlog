package apptive.devlog.Global.Auth.Attribute;

import apptive.devlog.Global.Auth.Dto.OauthMember;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    GOOGLE("google", (attribute) -> {
        OauthMember userProfile = new OauthMember();
        userProfile.setName((String)attribute.get("name"));
        userProfile.setEmail((String)attribute.get("email"));

        return userProfile;
    }),

    NAVER("naver", (attribute) -> {
        OauthMember userProfile = new OauthMember();

        Map<String, String> responseValue = (Map)attribute.get("response");

        userProfile.setName(responseValue.get("name"));
        userProfile.setEmail(responseValue.get("email"));

        return userProfile;
    }),

    KAKAO("kakao", (attribute) -> {

        Map<String, Object> account = (Map)attribute.get("kakao_account");
        Map<String, String> profile = (Map)account.get("profile");

        OauthMember oauthMember = new OauthMember();
        oauthMember.setName(profile.get("nickname"));
        oauthMember.setEmail((String)account.get("email"));

        return oauthMember;
    });

    private final String registrationId; // 로그인한 서비스(ex) google, naver..)
    private final Function<Map<String, Object>, OauthMember> of; // 로그인한 사용자의 정보를 통하여 유저정보 가져옴

    OAuthAttributes(String registrationId, Function<Map<String, Object>, OauthMember> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static OauthMember extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(value -> registrationId.equals(value.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}

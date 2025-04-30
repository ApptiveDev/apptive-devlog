package apptive.devlog.domain.oauth2.model;

import apptive.devlog.common.response.error.exception.InvalidOAuth2ResponseException;
import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.enums.Provider;
import apptive.devlog.domain.user.enums.Role;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class OAuth2Attributes {

    private final String nameAttributeKey;
    private final Provider provider;
    private final Map<String, Object> attributes;
    private final String email;
    private final String name;

    private OAuth2Attributes(String nameAttributeKey, Provider provider, Map<String, Object> attributes, String email, String name) {
        this.nameAttributeKey = nameAttributeKey;
        this.provider = provider;
        this.email = email;
        this.name = name;

        Map<String, Object> copied = new HashMap<>(attributes);
        copied.put("email", email);
        this.attributes = Collections.unmodifiableMap(copied);
    }

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            default -> throw new UnsupportedOperationException(registrationId);
        };
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuth2Attributes(
                userNameAttributeName,
                Provider.GOOGLE,
                attributes,
                (String) attributes.get("email"),
                (String) attributes.get("name")
        );
    }

    private static OAuth2Attributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = getMap(attributes, "response");

        return new OAuth2Attributes(
                "id",
                Provider.NAVER,
                response,
                (String) response.get("email"),
                (String) response.get("name")
        );
    }

    private static OAuth2Attributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = getMap(attributes, "kakao_account");
        Map<String, Object> profile = getMap(kakaoAccount, "profile");

        return new OAuth2Attributes(
                "id",
                Provider.KAKAO,
                attributes,
                (String) kakaoAccount.get("email"),
                (String) profile.get("nickname")
        );
    }

    private static Map<String, Object> getMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new InvalidOAuth2ResponseException(key);
    }

    public User toEntity() {
        return User.of(email, name, provider, Role.USER);
    }
}

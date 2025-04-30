package apptive.devlog.domain.oauth2.service;

import apptive.devlog.domain.oauth2.model.OAuth2Attributes;
import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        OAuth2Attributes attributes = extractAttributes(userRequest, oAuth2User);
        User user = saveOrUpdateUser(attributes);

        return createOAuth2User(user, attributes);
    }

    private OAuth2Attributes extractAttributes(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttr = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return OAuth2Attributes.of(registrationId, userNameAttr, oAuth2User.getAttributes());
    }

    private User saveOrUpdateUser(OAuth2Attributes attributes) {
        return userRepository.findByEmail(attributes.getEmail()).orElseGet(() -> userRepository.save(attributes.toEntity()));
    }

    private OAuth2User createOAuth2User(User user, OAuth2Attributes attributes) {
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }
}

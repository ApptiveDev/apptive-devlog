package apptive.devlog.Global.Auth.Service;

import apptive.devlog.Global.Auth.Attribute.OAuthAttributes;
import apptive.devlog.Global.Auth.Attribute.Provider;
import apptive.devlog.Global.Auth.Dto.OauthMember;
import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Member.Repository.MemberRepository;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OauthMember oauthMember = OAuthAttributes.extract(registrationId, attributes);
        oauthMember.setProvider(Provider.valueOf(registrationId));

        Member member = updateOrSaveUser(oauthMember);

        // ✅ JWT 생성 추가
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // ✅ 클라이언트가 사용할 수 있도록 커스텀 속성 추가
        Map<String, Object> customAttribute = new ConcurrentHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", member.getName());
        customAttribute.put("email", member.getEmail());
        customAttribute.put("accessToken", accessToken);
        customAttribute.put("refreshToken", refreshToken);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName);
    }

    public Member updateOrSaveUser(OauthMember oauthMember) {
        Member member = memberRepository
                .findByEmailAndProvider(oauthMember.getEmail(), oauthMember.getProvider())
                .map(value -> value.updateMember(oauthMember.getName(), oauthMember.getEmail()))
                .orElse(oauthMember.toEntity());
        member.setPassword(" ");

        return memberRepository.save(member);
    }
}

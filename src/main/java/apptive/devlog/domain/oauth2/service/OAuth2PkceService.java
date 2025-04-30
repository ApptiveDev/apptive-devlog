package apptive.devlog.domain.oauth2.service;

import apptive.devlog.domain.oauth2.dto.OAuth2CallbackRequestDto;
import apptive.devlog.domain.oauth2.dto.OAuth2CallbackResponseDto;
import apptive.devlog.common.response.error.exception.InvalidStateFormatException;
import apptive.devlog.common.response.error.exception.OAuth2TokenRequestException;
import apptive.devlog.common.response.error.exception.OAuth2UserInfoException;
import apptive.devlog.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2PkceService {

    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    public OAuth2CallbackResponseDto handleCallback(OAuth2CallbackRequestDto requestDto) {
        String code = requestDto.getCode();
        String state = requestDto.getState();
        String provider = requestDto.getProvider();

        String[] parts = state != null ? state.split("::") : new String[0];
        if (parts.length != 2) {
            throw new InvalidStateFormatException();
        }
        String deviceId = parts[0];
        String codeVerifier = parts[1];

        Map<String, Object> tokenResponse = requestAccessToken(provider, code, codeVerifier);
        String accessTokenFromProvider = Optional.ofNullable(tokenResponse.get("access_token"))
                .map(Object::toString)
                .orElseThrow(() -> new OAuth2TokenRequestException("Access token not found"));

        Map<String, Object> userInfo = fetchUserInfo(provider, accessTokenFromProvider);
        String email = extractEmail(provider, userInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        return new OAuth2CallbackResponseDto(accessToken, refreshToken);
    }

    private Map<String, Object> requestAccessToken(String provider, String code, String codeVerifier) {
        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        String tokenUri;
        switch (provider.toLowerCase()) {
            case "google" -> {
                tokenRequest.add("client_id", googleClientId);
                tokenRequest.add("client_secret", googleClientSecret);
                tokenRequest.add("grant_type", "authorization_code");
                tokenRequest.add("code", code);
                tokenRequest.add("redirect_uri", googleRedirectUri);
                tokenRequest.add("code_verifier", codeVerifier);
                tokenUri = "https://oauth2.googleapis.com/token";
            }
            case "kakao" -> {
                tokenRequest.add("grant_type", "authorization_code");
                tokenRequest.add("client_id", kakaoClientId);
                tokenRequest.add("client_secret", kakaoClientSecret);
                tokenRequest.add("code", code);
                tokenRequest.add("redirect_uri", kakaoRedirectUri);
                tokenUri = "https://kauth.kakao.com/oauth/token";
            }
            case "naver" -> {
                tokenRequest.add("grant_type", "authorization_code");
                tokenRequest.add("client_id", naverClientId);
                tokenRequest.add("client_secret", naverClientSecret);
                tokenRequest.add("code", code);
                tokenRequest.add("redirect_uri", naverRedirectUri);
                tokenUri = "https://nid.naver.com/oauth2.0/token";
            }
            default -> throw new OAuth2TokenRequestException("Unsupported OAuth2 provider: " + provider);
        }

        try {
            return webClient.post()
                    .uri(tokenUri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue(tokenRequest)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (WebClientResponseException ex) {
            throw new OAuth2TokenRequestException("Failed to get token from " + provider + ": " + ex.getResponseBodyAsString());
        }
    }

    private Map<String, Object> fetchUserInfo(String provider, String accessToken) {
        String userInfoUri = switch (provider.toLowerCase()) {
            case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo";
            case "kakao" -> "https://kapi.kakao.com/v2/user/me";
            case "naver" -> "https://openapi.naver.com/v1/nid/me";
            default -> throw new OAuth2UserInfoException("Unsupported OAuth2 provider: " + provider);
        };

        try {
            return webClient.get()
                    .uri(userInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (WebClientResponseException ex) {
            throw new OAuth2UserInfoException("Failed to get user info from " + provider + ": " + ex.getResponseBodyAsString());
        }
    }

    private String extractEmail(String provider, Map<String, Object> userInfo) {
        return switch (provider.toLowerCase()) {
            case "google" -> Optional.ofNullable(userInfo.get("email"))
                    .map(Object::toString)
                    .orElseThrow(() -> new OAuth2UserInfoException("Email not found in Google user info"));
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
                yield Optional.ofNullable(kakaoAccount.get("email"))
                        .map(Object::toString)
                        .orElseThrow(() -> new OAuth2UserInfoException("Email not found in Kakao user info"));
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
                yield Optional.ofNullable(response.get("email"))
                        .map(Object::toString)
                        .orElseThrow(() -> new OAuth2UserInfoException("Email not found in Naver user info"));
            }
            default -> throw new OAuth2UserInfoException("Unsupported OAuth2 provider: " + provider);
        };
    }
}

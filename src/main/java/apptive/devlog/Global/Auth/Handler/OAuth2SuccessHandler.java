package apptive.devlog.Global.Auth.Handler;

import apptive.devlog.Global.Auth.Jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // OAuth2User에서 사용자 정보 가져오기
        String email = (String) oAuth2User.getAttributes().get("email");

        // JWT 생성
        String token = jwtTokenProvider.createAccessToken(email);

        // 클라이언트에 JWT 반환 (HTTP 헤더)
        response.setHeader("Authorization", "Bearer " + token);
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        log.info("OAuth2 login successful. JWT issued for user: {}", email);
        // React로 리다이렉트하면서 토큰 전달

        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}

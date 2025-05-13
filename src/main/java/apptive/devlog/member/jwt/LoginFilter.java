package apptive.devlog.member.jwt;

import apptive.devlog.domain.RefreshEntity;
import apptive.devlog.member.dto.LoginForm;
import apptive.devlog.member.dto.MemberDetails;
import apptive.devlog.member.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override //필수 오버라이드
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginForm loginForm = new LoginForm();
        //@RequestBody기능을 사용할 수 없기 때문에 직접 문자열을 Json으로 파싱해줘야함
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, UTF_8);
            loginForm = objectMapper.readValue(messageBody, LoginForm.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        String email = loginForm.getEmail();
        String password = loginForm.getPassword();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password); //로그인 되지 않은 상태에서는 아이디와 비밀번호로 인증

        log.info("is Authenticated : LoginFilter = {}", authToken.isAuthenticated());
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName(); // email

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority(); // role

        String access = jwtUtil.createJWT("access", email, role, 600000L);
        String refresh = jwtUtil.createJWT("refresh", email, role, 86400000L);

        addRefreshEntity(email, refresh, 86400000L);

        response.setHeader("access",access);
        response.addCookie(createCookie("refresh",refresh));
        response.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity(email, refresh, date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true); //자바 스크립트로 쿠키 접근 금지
        return cookie;
    }
}

package apptive.devlog.member.jwt;

import apptive.devlog.domain.Member;
import apptive.devlog.member.dto.MemberDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("access");

        String requestURI = request.getRequestURI();

        if (accessToken == null || requestURI.equals("/login") || requestURI.equals("/reissue")
                || requestURI.equals("/connect/**") || requestURI.equals("/users")) {
            filterChain.doFilter(request,response);
            return;
        }
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.getWriter().write("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            response.getWriter().write("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        log.info("email: {}, role: {}", email, role);

        Member member = new Member(email, role); //로그인이 되었기 때문에 비밀번호 필요없음

        MemberDetails memberDetails = new MemberDetails(member);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken
                (memberDetails, null, memberDetails.getAuthorities());

        log.info("is Authenticated : JWTFilter = {}", authToken.isAuthenticated());

        SecurityContextHolder.getContext().setAuthentication(authToken); //다음 필터가 인증정보를 기억하기 위해

        filterChain.doFilter(request,response);
    }
}

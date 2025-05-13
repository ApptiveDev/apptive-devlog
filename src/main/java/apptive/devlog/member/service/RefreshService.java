package apptive.devlog.member.service;

import apptive.devlog.domain.RefreshEntity;
import apptive.devlog.member.exception.RefreshTokenValidateException;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
@RequiredArgsConstructor
@Service
public class RefreshService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public String[] validateRefreshToken(String refresh) {
        if (refresh == null) {
            throw new RefreshTokenValidateException("리프래시 토큰이 존재하지 않습니다");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenValidateException("리프래시 토큰이 만료되었습니다.");
        }

        String category = jwtUtil.getCategory(refresh);


        if (!category.equals("refresh")) {
            throw new RefreshTokenValidateException("리프래시 토큰이 아닙니다.");
        }

        if(!refreshRepository.existsByRefresh(refresh)) {
            throw new RefreshTokenValidateException("잘못된 리프래시 토큰입니다.");
        }

        String email = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String access = jwtUtil.createJWT("access", email, role, 600000L);
        String newRefresh = jwtUtil.createJWT("refresh", email, role, 86400000L);

        refreshRepository.deleteByEmail(email);
        addRefreshEntity(email, newRefresh, 86400000L);

        return new String[]{access, newRefresh};
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity(email, refresh, date.toString());

        refreshRepository.save(refreshEntity);
    }
}

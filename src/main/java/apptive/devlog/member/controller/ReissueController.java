package apptive.devlog.member.controller;

import apptive.devlog.domain.RefreshEntity;
import apptive.devlog.member.jwt.JWTUtil;
import apptive.devlog.member.repository.RefreshRepository;
import apptive.devlog.member.service.RefreshService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReissueController {

    private final RefreshService refreshService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        if (request.getCookies() == null)  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "리프래시 토큰이 존재하지 않습니다"));

        Cookie[] cookies = request.getCookies();

        String refresh = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refresh"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        String[] tokens = refreshService.validateRefreshToken(refresh);

        response.setHeader("access", tokens[0]);
        response.addCookie(createCookie("refresh",tokens[1]));

        return new ResponseEntity<>(HttpStatus.OK);

    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        return cookie;
    }
}


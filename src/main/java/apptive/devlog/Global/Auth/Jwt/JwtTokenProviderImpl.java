package apptive.devlog.Global.Auth.Jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private static final String SECRET_KEY = "mySecretKey";  // 비밀 키
    private static final long ACCESS_TOKEN_EXPIRATION = 3600 * 1000;  // 액세스 토큰 만료 시간 (1시간)
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3600 * 1000; // 리프레시 토큰 만료 (7일)

    @Override
    public boolean isValidToken(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (Exception e) {
            return false; // 예외가 발생하면 유효하지 않은 토큰으로 간주
        }
    }

    @Override
    public void invalidateToken(String token) {
        // 토큰을 무효화하는 로직 (토큰 검증 시 예외를 던지도록 하는 방식 사용)
    }

    @Override
    public String getEmailFromToken(String token) {
        return getUsername(token);
    }

    @Override
    public String createAccessToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    @Override
    public String createRefreshToken() {
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    @Override
    public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);
    }

    @Override
    public String getUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return verifyToken(token).getExpiresAt().before(new Date());
    }
}

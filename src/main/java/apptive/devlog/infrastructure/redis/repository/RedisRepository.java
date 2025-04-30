package apptive.devlog.infrastructure.redis.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    private static final String ACCESS_TOKEN_PREFIX = "AT:";
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisRepository(
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            RedisTemplate<String, String> redisTemplate) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.redisTemplate = redisTemplate;
    }

    public void saveAccessToken(String accessToken, String email) {
        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + accessToken, email, accessTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public void saveRefreshToken(String refreshToken, String email) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, email, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public boolean hasAccessToken(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(ACCESS_TOKEN_PREFIX + accessToken));
    }

    public boolean hasRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REFRESH_TOKEN_PREFIX + refreshToken));
    }

    public void deleteAccessToken(String accessToken) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + accessToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
    }
}

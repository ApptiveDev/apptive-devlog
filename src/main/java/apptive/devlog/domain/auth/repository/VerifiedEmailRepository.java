package apptive.devlog.domain.auth.repository;

import apptive.devlog.common.response.error.exception.InvalidEmailVerificationTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VerifiedEmailRepository {
    private static final String CODE_EMAIL_PREFIX = "CE:";
    private static final String VERIFY_EMAIL_PREFIX = "VE:";
    private final Duration EXPIRATION = Duration.ofMinutes(3);
    private final RedisTemplate<String, String> redisTemplate;

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(CODE_EMAIL_PREFIX + email, code, EXPIRATION);
    }

    public Optional<String> getCode(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(CODE_EMAIL_PREFIX + email));
    }

    public void deleteCode(String email) {
        redisTemplate.delete(CODE_EMAIL_PREFIX + email);
    }

    public String markVerified(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(VERIFY_EMAIL_PREFIX + email, token, EXPIRATION);
        return token;
    }

    public boolean isVerified(String email, String token) {
        return Boolean.TRUE.equals(Optional.ofNullable(redisTemplate.opsForValue().get(VERIFY_EMAIL_PREFIX + email))
                .map(storedToken -> Objects.equals(token, storedToken))
                .orElseThrow(InvalidEmailVerificationTokenException::new));
    }

    public void deleteVerified(String email) {
        redisTemplate.delete(VERIFY_EMAIL_PREFIX + email);
    }
}

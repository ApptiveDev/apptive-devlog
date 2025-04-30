package apptive.devlog.global.security.jwt;

import apptive.devlog.infrastructure.redis.repository.RedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final RedisRepository redisRepository;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            RedisRepository redisRepository) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.redisRepository = redisRepository;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateToken(String email, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expiration)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String email) {
        String accessToken = generateToken(email, accessTokenExpiration);
        redisRepository.saveAccessToken(accessToken, email);
        return accessToken;
    }

    public String generateRefreshToken(String email) {
        String refreshToken = generateToken(email, refreshTokenExpiration);
        redisRepository.saveRefreshToken(refreshToken, email);
        return refreshToken;
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed token: {}", e.getMessage());
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token) && redisRepository.hasAccessToken(token);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token) && redisRepository.hasRefreshToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

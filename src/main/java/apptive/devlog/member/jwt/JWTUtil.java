package apptive.devlog.member.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().get("role", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().getExpiration().before(new Date());
    }

    public String createJWT(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role",role)
                .issuedAt(new Date(System.currentTimeMillis())) //발행시간
                .expiration(new Date(System.currentTimeMillis()+expiredMs)) //소멸시간
                .signWith(secretKey) //암호화 진행
                .compact(); //토큰 생성
    }
}

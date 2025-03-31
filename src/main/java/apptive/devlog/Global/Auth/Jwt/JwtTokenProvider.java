package apptive.devlog.Global.Auth.Jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface JwtTokenProvider {
    boolean isValidToken(String token);
    void invalidateToken(String token);
    String getEmailFromToken(String token);

    String createAccessToken(String email);

    String createRefreshToken();

    DecodedJWT verifyToken(String token);

    String getUsername(String token);
}

package apptive.devlog.domain.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        setResponseHeader(response, HttpStatus.UNAUTHORIZED);
        writeErrorResponse(response, buildErrorResponse(exception));
    }

    private void setResponseHeader(HttpServletResponse response, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    private Map<String, Object> buildErrorResponse(AuthenticationException exception) {
        return Map.of("success", false, "error", "OAuth2 인증 실패", "message", exception.getMessage());
    }

    private void writeErrorResponse(HttpServletResponse response, Map<String, Object> body) throws IOException {
        String json = objectMapper.writeValueAsString(body);
        response.getWriter().write(json);
    }
}

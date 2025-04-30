package apptive.devlog.global.resolver;

import apptive.devlog.global.annotation.InjectToken;
import apptive.devlog.common.response.error.exception.InvalidTokenException;
import apptive.devlog.common.response.error.exception.TokenInjectionFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(0)
public class InjectTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(InjectToken.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        byte[] requestBodyBytes = getRequestBodyAsBytes(request);
        Object dto;
        try {
            dto = objectMapper.readValue(requestBodyBytes, parameter.getParameterType());
        } catch (IOException e) {
            log.error("DTO 역직렬화 실패: {}", e.getMessage(), e);
            throw new TokenInjectionFailedException("DTO 역직렬화 실패", e);
        }

        String accessToken = extractAccessToken(request);
        injectFieldIfExists(dto, "accessToken", accessToken);

        return dto;
    }

    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new InvalidTokenException();
    }

    private void injectFieldIfExists(Object target, String fieldName, String value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                field.set(target, value);
                log.debug("필드 '{}' 에 accessToken 주입 성공", fieldName);
            }
        } catch (NoSuchFieldException e) {
            log.warn("DTO에 '{}' 필드가 존재하지 않음: {}", fieldName, target.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            throw new TokenInjectionFailedException(fieldName, e);
        }
    }

    private byte[] getRequestBodyAsBytes(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}

package apptive.devlog.global.resolver;

import apptive.devlog.common.response.error.exception.DtoBindingFailedException;
import apptive.devlog.global.annotation.InjectEmail;
import apptive.devlog.common.response.error.exception.TokenInjectionFailedException;
import apptive.devlog.common.response.error.exception.UnauthenticatedUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.lang.reflect.Field;

@Slf4j
@Component
@RequiredArgsConstructor
public class InjectEmailArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(InjectEmail.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object dto = createDtoInstance(request, parameter.getParameterType());
        String email = extractCurrentUserEmail();

        injectFieldIfPresent(dto, "email", email);
        return dto;
    }

    private Object createDtoInstance(HttpServletRequest request, Class<?> parameterType) throws Exception {
        String method = request.getMethod().toUpperCase();
        try {
            if ("POST".equals(method) || "PUT".equals(method)) {
                return objectMapper.readValue(request.getInputStream(), parameterType);
            }
            return parameterType.getDeclaredConstructor().newInstance();
        } catch (IOException e) {
            throw new DtoBindingFailedException("[InjectEmailArgumentResolver] DTO 변환 실패", e);
        }
    }

    private String extractCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthenticatedUserException("현재 인증된 사용자가 없습니다.");
        }
        return authentication.getName();
    }

    private void injectFieldIfPresent(Object target, String fieldName, String value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                field.set(target, value);
            }
        } catch (NoSuchFieldException ignored) {
            log.debug("필드 '{}' 가 존재하지 않아 주입하지 않았습니다.", fieldName);
        } catch (IllegalAccessException e) {
            throw new TokenInjectionFailedException("필드 주입 실패: " + fieldName, e);
        }
    }
}

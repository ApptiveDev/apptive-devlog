package apptive.devlog.domain.auth.controller;

import apptive.devlog.domain.auth.dto.*;
import apptive.devlog.domain.auth.service.AuthService;
import apptive.devlog.domain.user.entity.User;
import apptive.devlog.domain.user.enums.Gender;
import apptive.devlog.domain.user.enums.Provider;
import apptive.devlog.domain.user.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTests {
        @Test
        @DisplayName("회원가입 성공")
        void signupSuccess() throws Exception {
            UserSignupRequestDto requestDto = new UserSignupRequestDto("test@example.com", "Password123!", "최광진", "qqwwee41", LocalDate.of(2000, 9, 26), Gender.MALE);
            UserSignupResponseDto responseDto = new UserSignupResponseDto(new User("test@example.com", "최광진", Provider.LOCAL, Role.USER));

            when(authService.signup(any(UserSignupRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/auth/signup").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)).characterEncoding("UTF-8"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTests {
        @Test
        void loginSuccess() throws Exception {
            UserLoginRequestDto requestDto = new UserLoginRequestDto("test@example.com", "Password123!");
            UserLoginResponseDto responseDto = new UserLoginResponseDto("access-token", "refresh-token");

            when(authService.login(any(UserLoginRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)).characterEncoding("UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
        }
    }

    @Nested
    @DisplayName("리프레시 테스트")
    class RefreshTests {
        @Test
        void refreshSuccess() throws Exception {
            UserRefreshRequestDto requestDto = new UserRefreshRequestDto("access-token", "refresh-token");
            UserRefreshResponseDto responseDto = new UserRefreshResponseDto("access-token", "refresh-token");

        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTests {
        @Test
        void logoutSuccess() throws Exception {
            UserLogoutRequestDto requestDto = new UserLogoutRequestDto("access-token", "refresh-token");

        }
    }
}

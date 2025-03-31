package apptive.devlog.Auth.Controller;

import apptive.devlog.Global.Auth.Controller.AuthController;
import apptive.devlog.Global.Auth.Dto.LoginRequestDto;
import apptive.devlog.Global.Auth.Service.AuthService;
import apptive.devlog.Global.Auth.Service.AuthServiceImpl;
import apptive.devlog.Global.Exception.InvalidTokenException;
import apptive.devlog.Global.Exception.MemberAlreadyLogoutException;
import apptive.devlog.Global.Exception.MemberNotExistException;
import apptive.devlog.Global.Response.Error.ErrorCode;
import apptive.devlog.Global.Response.Result.ResultCode;
import apptive.devlog.Member.Domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser // 테스트에서 인증된 사용자로 실행!
@ExtendWith(MockitoExtension.class) // Mockito 확장 활성화
@WebMvcTest(AuthController.class)   // 컨트롤러만 테스트
@MockitoBean(name = "authService",types = AuthServiceImpl.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Nested
    @DisplayName("로그인 테스트")
    class login{
        @Test
        @DisplayName("로그인 성공 테스트")
        void loginSuccess() throws Exception {
            // Given
            Member member = new Member();
            member.setEmail("test@example.com");
            member.setPassword("password");
            LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");

            // When
            when(authService.login(any(LoginRequestDto.class))).thenReturn(Optional.of(member)); // 로그인 성공

            // Then
            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(ResultCode.LOGIN_SUCCESS.getMessage()));
        }

        @Test
        @DisplayName("로그인 실패 테스트-아이디")
        void loginFail_InvalidId() throws Exception {
            // Given
            LoginRequestDto loginRequestDto = new LoginRequestDto("wrong@example.com", "wrongPassword");

            // When
            doThrow(new EntityNotFoundException("존재하지 않는 이메일입니다."))
                    .when(authService).login(any(LoginRequestDto.class));

            // Then
            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value("존재하지 않는 이메일입니다."));
        }
        @Test
        @DisplayName("로그인 실패 테스트-비밀번호")
        void loginFail_InvalidCredentials() throws Exception {
            // Given
            LoginRequestDto loginRequestDto = new LoginRequestDto("wrong@example.com", "wrongPassword");

            // When
            doThrow(new BadCredentialsException("비밀번호가 일치하지 않습니다."))
                    .when(authService).login(any(LoginRequestDto.class));



            // Then
            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("bad credentials"));
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class logout {
        @Test
        @DisplayName("로그아웃 성공 테스트")
        void logoutSuccess() throws Exception {
            // Given
            String token = "sampleToken";

            // When
            when(authService.logout(anyString())).thenReturn(true); // 로그아웃 예외 없이 동작

            // Then
            mockMvc.perform(post("/auth/logout")
                            .with(csrf())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(ResultCode.LOGOUT_SUCCESS.getMessage()));
        }

        @Test
        @DisplayName("로그아웃 실패 테스트")
        void logoutFail() throws Exception {
            // Given
            String token = "sampleToken";

            // When
            doThrow(new MemberAlreadyLogoutException())
                    .when(authService).logout(anyString());

            // Then
            mockMvc.perform(post("/auth/logout")
                            .with(csrf())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value(ErrorCode.MEMBER_ALREADY_LOGOUT.getMessage()));
        }

    }


    @Nested
    @DisplayName("탈퇴 테스트")
    class withdrawal{
        @Test
        @DisplayName("탈퇴 성공 테스트")
        void withdrawalSuccess() throws Exception {
            // Given
            String token = "sampleToken";

            // When
            when(authService.withdrawal(anyString()))
                    .thenReturn(true); // 탈퇴 예외 없이 동작

            // Then
            mockMvc.perform(post("/auth/withdrawal")
                            .with(csrf())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(ResultCode.DELETE_SUCCESS.getMessage()));
        }

        @Test
        @DisplayName("탈퇴 실패 테스트 - 유효하지않은 토큰")
        void withdrawalFail_InvalidToken() throws Exception {
            // Given
            String invalidToken = "invalidToken";

            // When
            doThrow(new InvalidTokenException())
                    .when(authService).withdrawal(anyString());

            // Then
            mockMvc.perform(post("/auth/withdrawal")
                            .with(csrf())
                            .header("Authorization", "Bearer " + invalidToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0].field").value(ErrorCode.INVALID_TOKEN.getMessage()));

        }

        @Test
        @DisplayName("탈퇴 실패 테스트 - 유효하지않은 유저")
        void withdrawalFail_InvalidMember() throws Exception {
            // Given
            String invalidToken = "invalidToken";

            // When
            doThrow(new MemberNotExistException())
                    .when(authService).withdrawal(anyString());

            // Then
            mockMvc.perform(post("/auth/withdrawal")
                            .with(csrf())
                            .header("Authorization", "Bearer " + invalidToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0].field").value(ErrorCode.MEMBER_NOT_EXIST.getMessage()));

        }
    }

}
